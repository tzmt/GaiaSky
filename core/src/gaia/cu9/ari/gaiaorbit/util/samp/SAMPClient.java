package gaia.cu9.ari.gaiaorbit.util.samp;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.astrogrid.samp.Message;
import org.astrogrid.samp.Metadata;
import org.astrogrid.samp.client.AbstractMessageHandler;
import org.astrogrid.samp.client.ClientProfile;
import org.astrogrid.samp.client.DefaultClientProfile;
import org.astrogrid.samp.client.HubConnection;
import org.astrogrid.samp.client.HubConnector;
import org.astrogrid.samp.client.SampException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import gaia.cu9.ari.gaiaorbit.GaiaSky;
import gaia.cu9.ari.gaiaorbit.data.group.STILDataProvider;
import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.event.IObserver;
import gaia.cu9.ari.gaiaorbit.scenegraph.StarGroup;
import gaia.cu9.ari.gaiaorbit.scenegraph.StarGroup.StarBean;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.CameraManager.CameraMode;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.Logger;
import gaia.cu9.ari.gaiaorbit.util.parse.Parser;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.util.URLDataSource;

public class SAMPClient implements IObserver {
    private static SAMPClient instance;

    public static SAMPClient getInstance() {
        if (instance == null)
            instance = new SAMPClient();
        return instance;
    }

    private HubConnector conn;
    private STILDataProvider provider;
    private Map<String, StarGroup> mapIdSg;
    private Map<String, String> mapIdUrl;
    private boolean preventProgrammaticEvents = false;

    public SAMPClient() {
        super();
        EventManager.instance.subscribe(this, Events.FOCUS_CHANGED, Events.DISPOSE);
    }

    public void initialize() {
        // Disable logging
        java.util.logging.Logger.getLogger("org.astrogrid.samp").setLevel(Level.OFF);

        // Init provider
        provider = new STILDataProvider();

        // Init map
        mapIdSg = new HashMap<String, StarGroup>();
        mapIdUrl = new HashMap<String, String>();

        ClientProfile cp = DefaultClientProfile.getProfile();
        conn = new GaiaSkyHubConnector(cp);

        // Configure it with metadata about this application
        Metadata meta = new Metadata();
        meta.setName(GlobalConf.APPLICATION_NAME);
        meta.setDescriptionText("3D Universe application focused on ESA's Gaia satellite");
        meta.setDocumentationUrl(GlobalConf.DOCUMENTATION);
        meta.setIconUrl(GlobalConf.ICON_URL);
        meta.put("author.name", GlobalConf.AUTHOR_NAME);
        meta.put("author.email", GlobalConf.AUTHOR_EMAIL);
        meta.put("author.affiliation", GlobalConf.AUTHOR_AFFILIATION);
        meta.put("home.page", GlobalConf.WEBPAGE);
        meta.put("gaiasky.version", GlobalConf.version.version);

        conn.declareMetadata(meta);

        // Load table
        conn.addMessageHandler(new AbstractMessageHandler("table.load.votable") {
            public Map processCall(HubConnection c, String senderId, Message msg) {
                // do stuff
                String name = (String) msg.getParam("name");
                String id = (String) msg.getParam("table-id");
                String url = (String) msg.getParam("url");

                boolean loaded = loadVOTable(url, id, name);

                if (loaded) {
                    Logger.info(SAMPClient.class.getSimpleName(), "VOTable " + name + " loaded successfully");
                } else {
                    Logger.info(SAMPClient.class.getSimpleName(), "Error loading VOTable " + name);
                }

                return null;
            }
        });

        // Select one row
        conn.addMessageHandler(new AbstractMessageHandler("table.highlight.row") {
            public Map processCall(HubConnection c, String senderId, Message msg) {
                // do stuff
                Long row = Parser.parseLong((String) msg.getParam("row"));
                String id = (String) msg.getParam("table-id");

                // First, fetch table if not here
                boolean loaded = mapIdSg.containsKey(id);

                // If table here, select
                if (loaded) {
                    Logger.info(SAMPClient.class.getSimpleName(), "Select row " + row + " of " + id);

                    if (mapIdSg.containsKey(id)) {
                        StarGroup sg = mapIdSg.get(id);
                        sg.setFocusIndex(row.intValue());
                        preventProgrammaticEvents = true;
                        EventManager.instance.post(Events.FOCUS_CHANGE_CMD, sg);
                        preventProgrammaticEvents = false;
                    }
                }
                return null;
            }
        });

        // Select multiple rows
        conn.addMessageHandler(new AbstractMessageHandler("table.select.rowList") {
            public Map processCall(HubConnection c, String senderId, Message msg) {
                // do stuff
                ArrayList<String> rows = (ArrayList<String>) msg.getParam("row-list");
                String id = (String) msg.getParam("table-id");

                // First, fetch table if not here
                boolean loaded = mapIdSg.containsKey(id);

                // If table here, select
                if (loaded && rows != null && !rows.isEmpty()) {
                    Logger.info(SAMPClient.class.getSimpleName(), "Select " + rows.size() + " rows of " + id + ". Gaia Sky does not support multiple selection, so only the first entry is selected.");
                    // We use the first one, as multiple selections are not supported in Gaia Sky
                    int row = Integer.parseInt(rows.get(0));
                    if (mapIdSg.containsKey(id)) {
                        StarGroup sg = mapIdSg.get(id);
                        sg.setFocusIndex(row);
                        preventProgrammaticEvents = true;
                        EventManager.instance.post(Events.FOCUS_CHANGE_CMD, sg);
                        preventProgrammaticEvents = false;
                    }
                }

                return null;
            }
        });

        // Point in sky
        conn.addMessageHandler(new AbstractMessageHandler("coord.pointAt.sky") {
            public Map processCall(HubConnection c, String senderId, Message msg) {
                // do stuff
                float ra = Float.parseFloat((String) msg.getParam("ra"));
                float dec = Float.parseFloat((String) msg.getParam("dec"));
                Logger.info(SAMPClient.class.getSimpleName(), "Point to coordinate (ra,dec): (" + ra + ", " + dec + ")");

                EventManager.instance.post(Events.CAMERA_MODE_CMD, CameraMode.Free_Camera);
                EventManager.instance.post(Events.FREE_MODE_COORD_CMD, ra, dec);

                return null;
            }
        });

        // This step required even if no custom message handlers added.
        conn.declareSubscriptions(conn.computeSubscriptions());

        // Keep a look out for hubs if initial one shuts down
        conn.setAutoconnect(10);

        // Broadcast a message
        //conn.getConnection().notifyAll(new Message("stuff.event.doing"));

    }

    /**
     * Loads a VOTable into a star group
     * @param url The URL to fetch the table
     * @param id The table id
     * @param name The table name
     * @return Boolean indicating whether loading succeeded or not
     */
    private boolean loadVOTable(String url, String id, String name) {
        Logger.info(SAMPClient.class.getSimpleName(), "Loading VOTable: " + name + " from " + url);

        try {
            DataSource ds = new URLDataSource(new URL(url));
            @SuppressWarnings("unchecked")
            Array<StarBean> data = (Array<StarBean>) provider.loadData(ds, 1.0f);

            if (data != null && data.size > 0) {
                StarGroup sg = new StarGroup();
                sg.setName(id);
                sg.setParent("Universe");
                sg.setFadeout(new double[] { 21e2, 1e5 });
                sg.setLabelcolor(new double[] { 1.0, 1.0, 1.0, 1.0 });
                sg.setColor(new double[] { 1.0, 1.0, 1.0, 0.25 });
                sg.setSize(6.0);
                sg.setLabelposition(new double[] { 0.0, -5.0e7, -4e8 });
                sg.setCt("Stars");
                sg.setData(data);

                mapIdSg.put(id, sg);
                mapIdUrl.put(id, url);

                // Insert
                Gdx.app.postRunnable(() -> {
                    sg.doneLoading(null);
                    GaiaSky.instance.sg.insert(sg, true);
                });
                Logger.info(data.size + " objects loaded via SAMP");
                return true;
            } else {
                // No data has been loaded
                return false;
            }
        } catch (Exception e) {
            Logger.error(e);
            return false;
        }
    }

    @Override
    public void notify(Events event, Object... data) {
        switch (event) {
        case FOCUS_CHANGED:
            if (!preventProgrammaticEvents && data[0] instanceof StarGroup) {
                StarGroup sg = (StarGroup) data[0];
                if (conn != null && conn.isConnected() && mapIdSg.containsValue(sg)) {
                    String id = sg.name;
                    String name = id;
                    String url = mapIdUrl.get(id);
                    int row = sg.getCandidateIndex();

                    Message msg = new Message("table.highlight.row");
                    msg.addParam("row", Integer.toString(row));
                    msg.addParam("table-id", id);
                    msg.addParam("url", url);

                    try {
                        conn.getConnection().notifyAll(msg);
                    } catch (SampException e) {
                        Logger.error(e);
                    }
                }
            }
            break;
        case DISPOSE:
            if (conn != null && conn.isConnected()) {
                conn.setActive(false);
            }
            break;
        }

    }

}

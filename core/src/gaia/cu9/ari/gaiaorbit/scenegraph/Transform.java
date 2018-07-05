package gaia.cu9.ari.gaiaorbit.scenegraph;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import gaia.cu9.ari.gaiaorbit.util.math.Matrix4d;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;

/**
 * Represents a geometric transformation. It can be either a matrix or a
 * position. Since we have such a vast amount of stars, it is convenient to be
 * able to represent translations with a position vector rather than with an
 * almost empty transform matrix.
 * 
 * @author Toni Sagrista
 *
 */
public class Transform {

    public Matrix4d transform;
    public Vector3d position;

    public Transform() {
        super();
    }

    public Transform(Vector3d position) {
        super();
        this.position = position;
    }

    /**
     * Sets this transform to represent the same as the other transform.
     * 
     * @param parent
     */
    public void set(Transform parent) {
        if (parent != null) {
            if (position != null) {
                if (parent.position != null) {
                    position.set(parent.position);
                } else if (parent.transform != null) {
                    // Vector > matrix
                    parent.transform.getTranslation(position);
                }
            } else if (transform != null) {
                if (parent.position != null) {
                    // Matrix > vector
                    transform.setTranslation(parent.position);
                } else if (parent.transform != null) {
                    // Matrix > matrix
                    transform.set(parent.transform);
                }
            }
        }
    }

    public void translate(Vector3d position) {
        if (this.transform != null) {
            this.transform.translate(position);
        } else if (this.position != null) {
            this.position.add(position);
        }
    }

    public void setToTranslation(Transform parent, Vector3d localPosition) {
        set(parent);
        translate(localPosition);
    }

    /**
     * Sets the given matrix to this transform
     * 
     * @param aux
     *            The out matrix
     * @return The matrix with the transform
     */
    public Matrix4d getMatrix(Matrix4d aux) {
        if (transform != null) {
            return aux.set(transform);
        } else if (position != null) {
            return aux.idt().translate(position);
        }
        return null;
    }

    public Matrix4 getMatrix(Matrix4 aux) {
        if (transform != null) {
            return transform.putIn(aux);
        } else if (position != null) {
            return aux.idt().translate((float) position.x, (float) position.y, (float) position.z);
        }
        return null;
    }

    public Vector3d getTranslation(Vector3d aux) {
        if (position != null) {
            return aux.set(position);
        } else if (transform != null) {
            return transform.getTranslation(aux);
        }
        return null;

    }

    public Vector3 getTranslationf(Vector3 aux) {
        if (position != null) {
            return aux.set(position.valuesf());
        } else if (transform != null) {
            return transform.getTranslationf(aux);
        } else {
            return aux;
        }
    }

    /**
     * Adds the translation of this object to the aux vector, and returns it for
     * chaining.
     * 
     * @param aux
     */
    public Vector3d addTranslationTo(Vector3d aux) {
        if (position != null) {
            return aux.add(position);
        } else if (transform != null) {
            return transform.addTranslationTo(aux);
        } else {
            return aux;
        }
    }

    public double[] getTranslation() {
        if (position != null) {
            return position.values();
        } else if (transform != null) {
            return transform.getTranslation();
        } else {
            return null;
        }
    }

    public float[] getTranslationf() {
        if (position != null) {
            return position.valuesf();
        } else if (transform != null) {
            return transform.getTranslationf();
        } else {
            return null;
        }
    }

    public void getTranslationf(float[] vec) {
        if (position != null) {
            position.valuesf(vec);
        } else if (transform != null) {
            transform.getTranslationf(vec);
        }
    }

    @Override
    public String toString() {
        if (position != null) {
            return position.toString();
        } else if (transform != null) {
            return transform.toString();
        } else {
            return super.toString();
        }
    }

}

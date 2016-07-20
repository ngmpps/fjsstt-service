package at.ngmpps.fjsstt.model;

/**
 * Proposed structure of a problem set using three Strings as used in
 * https://github.com/ngmpps/scenario/blob/master/Szenarien.md
 */
public class ProblemSet {

    /**
     * the String contained in the fjs-file
     */
    private String fjs;

    /**
     * the String contained in the transport file
     */
    private String transport;

    /**
     * the String contained in the properties file - as required by the Solver.
     */
    private String properties;

    public ProblemSet() {
    }

    public ProblemSet(String fjs, String transport, String properties) {
        this.fjs = fjs;
        this.transport = transport;
        this.properties = properties;
    }

    public String getFjs() {
        return fjs;
    }

    public String getTransport() {
        return transport;
    }

    public String getProperties() {
        return properties;
    }

    public void setFjs(String fjs) {
        this.fjs = fjs;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "ProblemSet{" +
                "fjs='" + fjs + '\'' +
                ", transport='" + transport + '\'' +
                ", properties='" + properties + '\'' +
                '}';
    }
}

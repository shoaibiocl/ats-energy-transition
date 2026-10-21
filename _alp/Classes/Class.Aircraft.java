
public class Aircraft implements Serializable {
	
	String id;             // unique name or number
    String distanceType;   // "short", "medium", "long"
    String fuelType;       // "kerosene", "saf", "hydrogen", etc.
    double age;               // current age (years)
    int lifetime;          // maximum life (years)

    /**
     * Default constructor
     */
    public Aircraft() {
    }
    
    public Aircraft(String id, String distanceType, String fuelType, int age, int lifetime) {
        this.id = id;
        this.distanceType = distanceType;
        this.fuelType = fuelType;
        this.age = age;
        this.lifetime = lifetime;
    }
    
    

	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * This number is here for model snapshot storing purpose<br>
	 * It needs to be changed when this class gets changed
	 */ 
	private static final long serialVersionUID = 1L;

}
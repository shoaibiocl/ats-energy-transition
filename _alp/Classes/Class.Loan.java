/**
 * Loan
 */	
public class Loan implements Serializable {

	public double principal;

	public double interestRate;

	public double term_years;

	public double age_years = 0.0;

    /**
     * Default constructor
     */
    public Loan() {
    }

    /**
     * Constructor initializing the fields
     */
    public Loan(double principal, double interestRate, double term_years) {
		this.principal = principal;
		this.interestRate = interestRate;
		this.term_years = term_years;
		
    }

	@Override
	public String toString() {
		return  
			"principal = " + principal +" " +
			"interestRate = " + interestRate +" " +
			"term_years = " + term_years +" " +
			"age_years = " + age_years +" ";
	}
	
	// Function to calculate this loan's annual payment
	public double getAnnualPayment() {
		if (age_years >= term_years) {
			return 0; //Loan in paid off
		}
		double r = interestRate;
        double n = term_years;
        double P = principal;
        // Standard annuity formula
        return P * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
    }

	/**
	 * This number is here for model snapshot storing purpose<br>
	 * It needs to be changed when this class gets changed
	 */ 
	private static final long serialVersionUID = 1L;

}
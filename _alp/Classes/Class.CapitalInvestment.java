/**
 * CapitalInvestment
 */	
public class CapitalInvestment implements Serializable {

	public double amountInvested;
	public double returnRate; 
	public double amortizationPeriod;

	public int periodsRemaining;

    /**
     * Default constructor
     */
    public CapitalInvestment() {
    }

    /**
     * Constructor initializing the fields
     */
    public CapitalInvestment(double amountInvested, double returnRate, int amortizationPeriod) {
        this.amountInvested = amountInvested;
        this.returnRate = returnRate;
        this.amortizationPeriod = amortizationPeriod;
        this.periodsRemaining = amortizationPeriod;
    }

    /**
     * Calculates the annual cost (annuity) of this specific investment.
     * @return The calculated annual cost.
     */
    public double getYearlyCost() {
        if (amountInvested <= 0) {
            return 0;
        }
        double P = this.amountInvested;
        double r = this.returnRate;
        double n = this.amortizationPeriod;
        
        // Standard annuity formula
        return P * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
    }
	private static final long serialVersionUID = 1L;

}
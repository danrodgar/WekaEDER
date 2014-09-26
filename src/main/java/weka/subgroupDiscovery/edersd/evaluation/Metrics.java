package weka.subgroupDiscovery.edersd.evaluation;

import java.util.ArrayList;

import weka.associations.AprioriItemSet;
import weka.core.Instance;
import weka.subgroupDiscovery.edersd.EDERSD_Rule;

public class Metrics {

	private static Metrics instance=new Metrics();
	
	private Metrics()
	{}
	
	public static Metrics getInstance()
	{
		return instance;		
	}
	
	public String calculateEvaluation(EDERSD_Rule rule, ArrayList<Instance> exampleSet)
	{					
		String result=		
				"  --> Metrics  <conf:("+redondearTwo(confidenceForRule(rule,exampleSet))+")>"		
				+" lift:("+redondearTwo(liftForRule(rule,exampleSet))
				+") lev:("+redondearTwo(leverageForRule(rule,exampleSet))
				+") conv:("+redondearTwo(convictionForRule(rule,exampleSet))+")";
				
				return result;
	}
	
	
	/**
	   * Outputs the confidence for a rule.
	   *
	   * @param premise the premise of the rule
	   * @param consequence the consequence of the rule
	   * @return the confidence on the training data
	   */
	  public double confidenceForRule(EDERSD_Rule rule, ArrayList<Instance> exampleSet) {

		  Confidence conf=new Confidence();
		  return conf.evaluate(rule, exampleSet, 0.0);
	  }

	  /**
	   * Outputs the lift for a rule. Lift is defined as:<br>
	   * confidence / prob(consequence)
	   *
	   * @param premise the premise of the rule
	   * @param consequence the consequence of the rule
	   * @param consequenceCount how many times the consequence occurs independent
	   * of the premise
	   * @return the lift on the training data
	   */
	  public double liftForRule(EDERSD_Rule rule, ArrayList<Instance> exampleSet) {
	    		  
		  Confidence conf=new Confidence();	
		  Support sup=new Support();
		  Coverage cov= new Coverage();
		  Prevalence prev=new Prevalence();
		  int n =exampleSet.size();
			
		  System.out.println("Confidence: "+conf.evaluate(rule, exampleSet, 0.0));
		  System.out.println("Prevalence "+prev.evaluate(rule, exampleSet, 0.0));
		  System.out.println("Support "+sup.evaluate(rule, exampleSet, 0.0));
		  System.out.println("Coverage "+cov.evaluate(rule, exampleSet, 0.0));
//		  
//		  		  	
//		  return (cov.evaluate(rule, exampleSet, 0.0))/prev.evaluate(rule, exampleSet, 0.0);
		  
		  return (conf.evaluate(rule, exampleSet, 0.0))/sup.evaluate(rule, exampleSet, 0.0);
		  //return (conf.evaluate(rule, exampleSet, 0.0)/(rule.nClass(exampleSet)/n));
		  
		  }

	  /**
	   * Outputs the leverage for a rule. Leverage is defined as: <br>
	   * prob(premise & consequence) - (prob(premise) * prob(consequence))
	   *
	   * @param premise the premise of the rule
	   * @param consequence the consequence of the rule
	   * @param premiseCount how many times the premise occurs independent
	   * of the consequent
	   * @param consequenceCount how many times the consequence occurs independent
	   * of the premise
	   * @return the leverage on the training data
	   */
	  public double leverageForRule(EDERSD_Rule rule, ArrayList<Instance> exampleSet) {
		  		  
		  Confidence conf=new Confidence();
		  Coverage cov=new Coverage();
		  Prevalence prev=new Prevalence();
		  Support sup=new Support();
		  
		  //return ((cov.evaluate(rule, exampleSet, 0.0)+ prev.evaluate(rule, exampleSet, 0.0) -sup.evaluate(rule, exampleSet, 0.0)) - (cov.evaluate(rule, exampleSet, 0.0)*prev.evaluate(rule, exampleSet, 0.0)));
		  double expectedCoverageIfIndependent=prev.evaluate(rule, exampleSet, 0.0)*cov.evaluate(rule, exampleSet, 0.0);
		  
		  return cov.evaluate(rule, exampleSet, 0.0)-expectedCoverageIfIndependent;
			 		  
	  }

	  /**
	   * Outputs the conviction for a rule. Conviction is defined as: <br>
	   * prob(premise) * prob(!consequence) / prob(premise & !consequence)
	   *
	   * @param premise the premise of the rule
	   * @param consequence the consequence of the rule
	   * @param premiseCount how many times the premise occurs independent
	   * of the consequent
	   * @param consequenceCount how many times the consequence occurs independent
	   * of the premise
	   * @return the conviction on the training data
	   */
	  public double convictionForRule(EDERSD_Rule rule, ArrayList<Instance> exampleSet) {
		  		  
		  Prevalence prev=new Prevalence();
		  Confidence conf=new Confidence();
		  
		  double nClass = rule.nClass(exampleSet);
		  double nCond = rule.nClass(exampleSet);
		  double N = exampleSet.size();

		  //return (rule.nCond(exampleSet)*rule.notnClass(exampleSet))/rule.nCondnotClass(exampleSet);
		  
		  return (1-prev.evaluate(rule, exampleSet, 0.0))/(1-conf.evaluate(rule, exampleSet, 0.0));
		  
		  //return (nClass*(N-nCond)/N)/((nClass-nCond)+1);
	  }
	  
	  /**
	   * To round a value to two decimals.
	   * @param d
	   * @return double
	*/
	  private double redondearTwo(double d) {
			return Math.rint(d * 100) / 100;
	  }	  

}

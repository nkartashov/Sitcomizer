package AU.MightyFour.Sitcomizer;

/**
 * Created with IntelliJ IDEA.
 * User: nikita_kartashov
 * Date: 11/11/2013
 * Time: 18:26
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationContext
{
	public ApplicationContext getContext() {return _context;}

	private ApplicationContext()
	{
		//Bluetooth init code goes here
	}

	private ApplicationContext _context = new ApplicationContext();
}

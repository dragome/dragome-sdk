package java.util.logging;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Supplier;

public class Logger
{

	@Deprecated
	public static final Logger global= new Logger();

	private Level level= Level.INFO;
	private Calendar calendar= Calendar.getInstance();

	public void info(String msg)
	{
		log(Level.INFO, msg);
	}

	public void finer(String msg)
	{
		log(Level.FINER, msg);
	}

	private String fill(int i)
	{
		return (i < 10 ? "0" : "") + Integer.toString(i);
	}

	public void log(Level theLevel, String msg)
	{
		if (theLevel.intValue() < level.intValue())
			return;
		calendar.setTime(new Date());

		System.out.println("[" + fill(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + fill(calendar.get(Calendar.MINUTE)) + ":" + fill(calendar.get(Calendar.SECOND)) + "] " + msg);
	}

	/**
	 * Get the log Level that has been specified for this Logger.
	 */
	public Level getLevel()
	{
		return level;
	}

	/**
	 * Set the log level specifying which message levels will be logged by this logger.
	 * Message levels lower than this value will be discarded. The level value Level.OFF
	 * can be used to turn off logging.
	 */
	public void setLevel(Level newLevel)
	{
		level= newLevel;
	}

	public static Logger getLogger(String string)
	{
		return new Logger();
	}

	public void log(Level level, String msg, Throwable e)
	{
		if (Level.INFO.equals(level))
		{
			System.out.println(msg);
			System.out.println(e.getMessage());
		}
	}

	public void finest(String msg)
	{
		log(Level.FINEST, msg);
	}

	public void severe(String msg)
	{
		log(Level.SEVERE, msg);
	}

	public void warning(String msg)
	{
		log(Level.WARNING, msg);
	}

    public void config(String msg) {
        log(Level.CONFIG, msg);
    }

    public void fine(String msg) {
        log(Level.FINE, msg);
    }

    public void severe(Supplier<String> msgSupplier) {
        log(Level.SEVERE, msgSupplier);
    }

    private void log(Level level, Supplier<String> msgSupplier)
	{
    	log(level, msgSupplier.get());
	}

	public void warning(Supplier<String> msgSupplier) {
        log(Level.WARNING, msgSupplier);
    }

    public void info(Supplier<String> msgSupplier) {
        log(Level.INFO, msgSupplier);
    }

    public void config(Supplier<String> msgSupplier) {
        log(Level.CONFIG, msgSupplier);
    }

    public void fine(Supplier<String> msgSupplier) {
        log(Level.FINE, msgSupplier);
    }

    public void finer(Supplier<String> msgSupplier) {
        log(Level.FINER, msgSupplier);
    }

    public void finest(Supplier<String> msgSupplier) {
        log(Level.FINEST, msgSupplier);
    }
}

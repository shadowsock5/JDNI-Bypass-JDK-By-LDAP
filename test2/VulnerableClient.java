/*
 * javac -encoding GBK -g VulnerableClient.java
 */
import javax.naming.*;

public class VulnerableClient
{
    public static void main ( String[] argv ) throws Exception
    {
        String  name    = argv[0];
        Context ctx     = new InitialContext();
        ctx.lookup( name );
    }
}

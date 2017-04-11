package TestZook.util;

/**
 * Created by weichen on 17/4/10.
 * 参考：https://github.com/alibaba/taokeeper/blob/master/taokeeper-research/src/main/java/com/taobao/taokeeper/research/watcher/AllZooKeeperWatcher.java
 */

/*

实现Watcher接口

例子中包含连个线程；main线程和Watcher 的watcher线程；
线程间通信，使用CountDownLatch 完成

*/

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.PropertyConfigurator;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookTest  implements Watcher{

    private static final Logger LOG = LoggerFactory.getLogger( ZookTest.class );
    /*定义原子变量*/
    AtomicInteger seq = new AtomicInteger();
    /*定义超时时间*/
    private static final int SESSION_TIMEOUT = 10000;

//    private static final String CONNECTION_STRING = "test.zookeeper.connection_string:2181," +
//            "test.zookeeper.connection_string2:2181," +
//            "test.zookeeper.connection_string3:2181";
    /*zookeeper 服务器地址*/
    private static final String CONNECTION_STRING = "192.168.178.151:2181," +
            "192.168.178.152:2181," +
            "192.168.178.153:2181";
    //zk 路径设置
    private static final String ZK_PATH 				= "/nileader";
    //zk  子路径设置
    private static final String CHILDREN_PATH 	= "/nileader/ch";
    //标识
    private static final String LOG_PREFIX_OF_MAIN = "【Main】";

    //zk 实例子
    private ZooKeeper zk = null;

    //用于等待zk连接成功，通知阻塞程序继续运行
    private CountDownLatch connectedSemaphore = new CountDownLatch( 1 );

    /**
     * 创建ZK连接
     * @param connectString	 ZK服务器地址列表
     * @param sessionTimeout   Session超时时间
     */
    public void createConnection( String connectString, int sessionTimeout ) {
        this.releaseConnection();
        try {
            zk = new ZooKeeper( connectString, sessionTimeout,this );
            LOG.info( LOG_PREFIX_OF_MAIN + "开始连接ZK服务器" );
            /* ==== 阻塞进程 ==== */
            connectedSemaphore.await();
        } catch ( Exception e ) {}
    }

    /**
     * 关闭ZK连接
     */
    public void releaseConnection() {
        if ( this.zk != null) {
            try {
                this.zk.close();
            } catch ( InterruptedException e ) {}
        }
    }

    /**
     *  创建节点
     * @param path 节点path
     * @param data 初始数据内容
     * @return
     */
    public boolean createPath( String path, String data ) {
        try {
            /*设置监控；（zk每次变化监控是一次性的，所以必须一致监控）*/
            this.zk.exists( path, true);
            LOG.info( LOG_PREFIX_OF_MAIN + "节点创建成功, Path: "
                    + this.zk.create( path, //
                    data.getBytes(),        //
                    Ids.OPEN_ACL_UNSAFE,    //
                    CreateMode.PERSISTENT )
                    + ", content: " + data );
        } catch ( Exception e ) {}
        return true;
    }

    /**
     * 读取指定节点数据内容
     * @param path 节点path
     * @return
     */
    public String readData( String path, boolean needWatch ) {
        try {
            return new String( this.zk.getData( path, needWatch, null ) );
        } catch ( Exception e ) {
            return "";
        }
    }

    /**
     * 更新指定节点数据内容
     * @param path 节点path
     * @param data  数据内容
     * @return
     */
    public boolean writeData( String path, String data ) {
        try {
            LOG.info( LOG_PREFIX_OF_MAIN + "更新数据成功，path：" + path + ", stat: " +
                    this.zk.setData( path, data.getBytes(), -1 ) );
        } catch ( Exception e ) {}
        return false;
    }

    /**
     * 删除指定节点
     * @param path 节点path
     */
    public void deleteNode( String path ) {
        try {
            this.zk.delete( path, -1 );
            LOG.info( LOG_PREFIX_OF_MAIN + "删除节点成功，path：" + path );
        } catch ( Exception e ) {
            //TODO
        }
    }

    /**
     * 删除指定节点
     * @param path 节点path
     */
    public Stat exists( String path, boolean needWatch ) {
        try {
            return this.zk.exists( path, needWatch );
        } catch ( Exception e ) {return null;}
    }

    /**
     * 获取子节点
     * @param path 节点path
     */
    private List<String> getChildren( String path, boolean needWatch ) {
        try {
            return this.zk.getChildren( path, needWatch );
        } catch ( Exception e ) {return null;}
    }

    public void deleteAllTestPath(){
        this.deleteNode( CHILDREN_PATH );
        this.deleteNode( ZK_PATH );
    }

    public static void main( String[] args ) throws InterruptedException {

        PropertyConfigurator.configure("src/main/resources/log4j.properties");

        /*创建zk连接*/
        ZookTest sample = new ZookTest();
        sample.createConnection( CONNECTION_STRING, SESSION_TIMEOUT );
        //清理节点
        sample.deleteAllTestPath();

        /*---------第一步：创建父目录节点--------*/
        if ( sample.createPath( ZK_PATH, System.currentTimeMillis()+"" ) ) {

            Thread.sleep( 3000 );
             /*---------第二步：读取数据--------*/
            sample.readData( ZK_PATH, true );

            /*---------第三步：读取子节点--------*/
            sample.getChildren( ZK_PATH, true );

             /*---------第四步：更新数据--------*/
            sample.writeData( ZK_PATH, System.currentTimeMillis()+"" );

            Thread.sleep( 3000 );
            /*---------第五步：创建子节点--------*/
            sample.createPath( CHILDREN_PATH, System.currentTimeMillis()+"" );
        }
        Thread.sleep( 3000 );
        /*清理节点*/
        sample.deleteAllTestPath();
        Thread.sleep( 3000 );
        /*释放连接*/
        sample.releaseConnection();
    }

    /**
     * 收到来自Server的Watcher通知后的处理。
     * process 单独的一个线程；与zook异步线程；不堵塞住线程
     */

    /* ================ 重写 watcher 的process 方法 ================ */
    public void process( WatchedEvent event ) {

        System.out.println("\n 进入process 线程 ... Even:" + event);
        try {
            Thread.sleep( 200 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ( event == null ) {
            return;
        }

        /*连接状态   (Disconnected|NoSyncConnected|SyncConnected|AuthFailed|ConnectedReadOnly|SaslAuthenticated);*/
        KeeperState keeperState = event.getState();
        /*事件类型   (NodeCreated|NodeDeleted|NodeDataChanged|NodeChildrenChanged);*/
        EventType eventType = event.getType();
        /*受影响的path （/path/child_path）*/
        String path = event.getPath();
        /*seq.incrementAndGet() 线程安全的；对每次zook变化，进行计数+1*/
        String logPrefix = "【Watcher-" + this.seq.incrementAndGet() + "】";

        LOG.info( logPrefix + "收到Watcher通知" );
        LOG.info( logPrefix + "连接状态:\t" + keeperState.toString() );
        LOG.info( logPrefix + "事件类型:\t" + eventType.toString() );

        if ( KeeperState.SyncConnected == keeperState ) {
            // 成功连接上ZK服务器;
            if ( EventType.None == eventType ) {
                /*EventType.None 表示，连接成功；通过 countDown 通知 "主线程" 继续运行*/
                LOG.info( logPrefix + "成功连接上ZK服务器" );
                connectedSemaphore.countDown();

            } else if ( EventType.NodeCreated == eventType ) {
                /*EventType.NodeCreated 创建节点事件*/
                LOG.info( logPrefix + "节点创建" );
                this.exists( path, true );

            } else if ( EventType.NodeDataChanged == eventType ) {
                /*EventType.NodeDataChanged 节点内容进行了更新*/
                LOG.info( logPrefix + "节点数据更新" );
                LOG.info( logPrefix + "数据内容: " + this.readData( ZK_PATH, true ) );

            } else if ( EventType.NodeChildrenChanged == eventType ) {
                /*EventType.NodeChildrenChanged 创建子子节点*/
                LOG.info( logPrefix + "子节点变更" );
                LOG.info( logPrefix + "子节点列表：" + this.getChildren( ZK_PATH, true ) );
            } else if ( EventType.NodeDeleted == eventType ) {
                LOG.info( logPrefix + "节点 " + path + " 被删除" );
            }

        } else if ( KeeperState.Disconnected == keeperState ) {
            LOG.info( logPrefix + "与ZK服务器断开连接" );
        } else if ( KeeperState.AuthFailed == keeperState ) {
            LOG.info( logPrefix + "权限检查失败" );
        } else if ( KeeperState.Expired == keeperState ) {
            LOG.info( logPrefix + "会话失效" );
        }

        LOG.info( "--------------------------------------------" );

    }
}

/*

2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:zookeeper.version=3.4.6-1569965, built on 02/20/2014 09:09 GMT
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:host.name=172-15-70-16.lightspeed.austtx.sbcglobal.net
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:java.version=1.8.0_51
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:java.vendor=Oracle Corporation
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:java.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:java.class.path=/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/deploy.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/ext/jfxrt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/ext/nashorn.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/javaws.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/jfxswt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/plugin.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/lib/ant-javafx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/lib/javafx-mx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/lib/packager.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/lib/sa-jdi.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/lib/tools.jar:/Users/weichen/IdeaProjects/ConCurrencyDemo/target/classes:/Users/weichen/.m2/repository/org/apache/zookeeper/zookeeper/3.4.6/zookeeper-3.4.6.jar:/Users/weichen/.m2/repository/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar:/Users/weichen/.m2/repository/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar:/Users/weichen/.m2/repository/log4j/log4j/1.2.16/log4j-1.2.16.jar:/Users/weichen/.m2/repository/jline/jline/0.9.94/jline-0.9.94.jar:/Users/weichen/.m2/repository/junit/junit/3.8.1/junit-3.8.1.jar:/Users/weichen/.m2/repository/io/netty/netty/3.7.0.Final/netty-3.7.0.Final.jar:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:java.library.path=/Users/weichen/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:java.io.tmpdir=/var/folders/dp/chfcfx3d3d5gnx8_cdsmy49w0000gn/T/
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:java.compiler=<NA>
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:os.name=Mac OS X
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:os.arch=x86_64
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:os.version=10.11.2
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:user.name=weichen
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:user.home=/Users/weichen
2017-04-10 17:03:57 INFO  ZooKeeper:100 - Client environment:user.dir=/Users/weichen/IdeaProjects/ConCurrencyDemo
2017-04-10 17:03:57 INFO  ZooKeeper:438 - Initiating client connection, connectString=192.168.178.151:2181,192.168.178.152:2181,192.168.178.153:2181 sessionTimeout=10000 watcher=TestZook.util.ZookTest@5f4da5c3
2017-04-10 17:03:57 INFO  ZookTest:61 - 【Main】开始连接ZK服务器
2017-04-10 17:03:57 INFO  ClientCnxn:975 - Opening socket connection to server 192.168.178.152/192.168.178.152:2181. Will not attempt to authenticate using SASL (unknown error)
2017-04-10 17:03:57 INFO  ClientCnxn:852 - Socket connection established to 192.168.178.152/192.168.178.152:2181, initiating session
2017-04-10 17:03:57 INFO  ClientCnxn:1235 - Session establishment complete on server 192.168.178.152/192.168.178.152:2181, sessionid = 0x25b4138a8990006, negotiated timeout = 10000

 进入process 线程 ... Even:WatchedEvent state:SyncConnected type:None path:null
2017-04-10 17:03:57 INFO  ZookTest:223 - 【Watcher-1】收到Watcher通知
2017-04-10 17:03:57 INFO  ZookTest:224 - 【Watcher-1】连接状态:	SyncConnected
2017-04-10 17:03:57 INFO  ZookTest:225 - 【Watcher-1】事件类型:	None
2017-04-10 17:03:57 INFO  ZookTest:231 - 【Watcher-1】成功连接上ZK服务器
2017-04-10 17:03:57 INFO  ZookTest:260 - --------------------------------------------
2017-04-10 17:03:57 INFO  ZookTest:132 - 【Main】删除节点成功，path：/nileader/ch
2017-04-10 17:03:57 INFO  ZookTest:132 - 【Main】删除节点成功，path：/nileader

 进入process 线程 ... Even:WatchedEvent state:SyncConnected type:NodeCreated path:/nileader
2017-04-10 17:03:57 INFO  ZookTest:88 - 【Main】节点创建成功, Path: /nileader, content: 1491815037764
2017-04-10 17:03:57 INFO  ZookTest:223 - 【Watcher-2】收到Watcher通知
2017-04-10 17:03:57 INFO  ZookTest:224 - 【Watcher-2】连接状态:	SyncConnected
2017-04-10 17:03:57 INFO  ZookTest:225 - 【Watcher-2】事件类型:	NodeCreated
2017-04-10 17:03:57 INFO  ZookTest:236 - 【Watcher-2】节点创建
2017-04-10 17:03:57 INFO  ZookTest:260 - --------------------------------------------

 进入process 线程 ... Even:WatchedEvent state:SyncConnected type:NodeDataChanged path:/nileader
2017-04-10 17:04:00 INFO  ZookTest:119 - 【Main】更新数据成功，path：/nileader, stat: 8589934734,8589934735,1491815037775,1491815040790,1,0,0,0,13,0,8589934734

2017-04-10 17:04:00 INFO  ZookTest:223 - 【Watcher-3】收到Watcher通知
2017-04-10 17:04:00 INFO  ZookTest:224 - 【Watcher-3】连接状态:	SyncConnected
2017-04-10 17:04:00 INFO  ZookTest:225 - 【Watcher-3】事件类型:	NodeDataChanged
2017-04-10 17:04:00 INFO  ZookTest:241 - 【Watcher-3】节点数据更新
2017-04-10 17:04:00 INFO  ZookTest:242 - 【Watcher-3】数据内容: 1491815040789
2017-04-10 17:04:00 INFO  ZookTest:260 - --------------------------------------------

 进入process 线程 ... Even:WatchedEvent state:SyncConnected type:NodeCreated path:/nileader/ch
2017-04-10 17:04:03 INFO  ZookTest:88 - 【Main】节点创建成功, Path: /nileader/ch, content: 1491815043797
2017-04-10 17:04:04 INFO  ZookTest:223 - 【Watcher-4】收到Watcher通知
2017-04-10 17:04:04 INFO  ZookTest:224 - 【Watcher-4】连接状态:	SyncConnected
2017-04-10 17:04:04 INFO  ZookTest:225 - 【Watcher-4】事件类型:	NodeCreated
2017-04-10 17:04:04 INFO  ZookTest:236 - 【Watcher-4】节点创建
2017-04-10 17:04:04 INFO  ZookTest:260 - --------------------------------------------

 进入process 线程 ... Even:WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/nileader
2017-04-10 17:04:04 INFO  ZookTest:223 - 【Watcher-5】收到Watcher通知
2017-04-10 17:04:04 INFO  ZookTest:224 - 【Watcher-5】连接状态:	SyncConnected
2017-04-10 17:04:04 INFO  ZookTest:225 - 【Watcher-5】事件类型:	NodeChildrenChanged
2017-04-10 17:04:04 INFO  ZookTest:246 - 【Watcher-5】子节点变更
2017-04-10 17:04:04 INFO  ZookTest:247 - 【Watcher-5】子节点列表：[ch]
2017-04-10 17:04:04 INFO  ZookTest:260 - --------------------------------------------
2017-04-10 17:04:09 INFO  ZooKeeper:684 - Session: 0x25b4138a8990006 closed
2017-04-10 17:04:09 INFO  ClientCnxn:512 - EventThread shut down

Process finished with exit code 0

*/
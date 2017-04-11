package TestZook;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * Created by weichen on 17/4/6.
 */
public class ZookDemo {

    public static void main(String[] args) throws IOException{
        System.out.println("zook");
        //1.创建一个与zook服务器的链接

        ZooKeeper zk = new ZooKeeper("192.168.178.151:2181", 60000, new Watcher() {
            // 监控所有被触发的事件
            // 当对目录节点监控状态打开时，一旦目录节点的状态发生变化，Watcher 对象的 process 方法就会被调用。
            public void process(WatchedEvent watchedEvent) {
                System.out.println("EVENT:" + watchedEvent.getPath());
            }
        });

//      2. 查看根节点信息
//          获取指定 path 下的所有子目录节点，同样 getChildren方法也有一个重载方法可以设置特定的 watcher 监控子节点的状态
        try {
            System.out.println("ls / =>" + zk.getChildren("/",true));


//          3. 创建一个目录节点

            // 创建一个给定的目录节点 path, 并给它设置数据；
            // CreateMode 标识有四种形式的目录节点，分别是：
            //     PERSISTENT：持久化目录节点，这个目录节点存储的数据不会丢失；
            //     PERSISTENT_SEQUENTIAL：顺序自动编号的目录节点，这种目录节点会根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名；
            //     EPHEMERAL：临时目录节点，一旦创建这个节点的客户端与服务器端口也就是 session 超时，这种节点会被自动删除；
            //     EPHEMERAL_SEQUENTIAL：临时自动编号节点

            if (zk.exists("/tt/tt02",true) == null) {
                zk.create("/tt/tt02", "hanmeimei".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
                System.out.println("create /tt/tt02 hanmeimei");
                System.out.println("get /tt/tt02 => " + new String(zk.getData("/tt/tt02", false, null)));
            }

//          4.修改节点
            if (zk.exists("/tt/tt02",true) != null) {
                zk.setData("/tt/tt02","hanmeimei02".getBytes(),-1);
                System.out.println("set /tt/tt02 hanmeimei02");
                System.out.println("get /tt/tt02 => " + new String(zk.getData("/tt/tt02", false, null)));
            }

            //          4.1.修改节点
            if (zk.exists("/tt/tt02",true) != null) {
                System.out.println("set /tt list " + zk.getChildren("/tt",false).toString());
            }

//            5.删除节点
            if (zk.exists("/tt/tt02_dele",true) == null) {
                zk.create("/tt/tt02_dele", "hanmeimei_dele".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
                System.out.println("set /tt/tt02_dele hanmeimei_dele");

                System.out.println("get /tt/tt02 => " + new String(zk.getData("/tt/tt02_dele", false, null)));
                zk.delete("/tt/tt02_dele",-1);

                if (zk.exists("/tt/tt02_dele",true) == null) {
                    System.out.println("delete success!");
                }
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //关闭zk链接
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        // 查看根节点
//        System.out.println("ls / => " + zk.getChildren("/", true));
//
//        // 创建一个目录节点
//        if (zk.exists("/node", true) == null) {
//            zk.create("/node", "conan".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//            System.out.println("create /node conan");
//            // 查看/node节点数据
//            System.out.println("get /node => " + new String(zk.getData("/node", false, null)));
//            // 查看根节点
//            System.out.println("ls / => " + zk.getChildren("/", true));
//        }
//
//        // 创建一个子目录节点
//        if (zk.exists("/node/sub1", true) == null) {
//            zk.create("/node/sub1", "sub1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//            System.out.println("create /node/sub1 sub1");
//            // 查看node节点
//            System.out.println("ls /node => " + zk.getChildren("/node", true));
//        }
//
//        // 修改节点数据
//        if (zk.exists("/node", true) != null) {
//            zk.setData("/node", "changed".getBytes(), -1);
//            // 查看/node节点数据
//            System.out.println("get /node => " + new String(zk.getData("/node", false, null)));
//        }
//
//        // 删除节点
//        if (zk.exists("/node/sub1", true) != null) {
//            zk.delete("/node/sub1", -1);
//            zk.delete("/node", -1);
//            // 查看根节点
//            System.out.println("ls / => " + zk.getChildren("/", true));
//        }
//
//        // 关闭连接
//        zk.close();
    }
}

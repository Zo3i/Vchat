package com.jo;

import com.jo.config.ResourceConfig;
import com.jo.enums.BgmOperatTypeEnum;
import com.jo.pojo.Bgm;
import com.jo.service.BgmService;
import com.jo.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

public class ZKCuratorClient {
    //zk客户端
    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

    @Autowired
    private ResourceConfig resourceConfig;

    public void init() {

        String ZOOKEEPER_SERVER = resourceConfig.getZookeeperServer();
        log.info("init ZK");
        if (client != null) {
            return;
        }
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,5);
        //创建客户端
        client = CuratorFrameworkFactory.builder().connectString(ZOOKEEPER_SERVER)
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin")
                .build();
        //启动客户端
        client.start();
        try {
            //监听子节点BGM
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChildWatch(String nodePath) throws Exception{
        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                    throws Exception {
                //触发添加事件
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {

                    String path = event.getData().getPath();
                    String typeMap = new String(event.getData().getData());
                    Map<String,String> map = JsonUtils.jsonToPojo(typeMap,Map.class);
                    String opType = map.get("opType");
                    String bgmPath =map.get("path");
                    log.info("BGM path :" +bgmPath);
                    //保存到本地的目录
                    String localPath = resourceConfig.getFileSpace() + bgmPath;
                    //定义保存路径
                    String arrPath[] = bgmPath.split("\\\\");
                    String finalPath = "";
                    for (int i = 0; i < arrPath.length; i++) {
                        if (StringUtils.isNotBlank(arrPath[i])) {
                            finalPath += "/";
                            finalPath += URLEncoder.encode(new String(arrPath[i].getBytes()),"UTF-8");
                        }
                    }
                    log.info("保存路径:" + finalPath);
                    //下载路径
                    String bgmUrl = resourceConfig.getBgmServer() + finalPath;
                    log.info("下载路径"+bgmUrl);

                    if(opType.equals(BgmOperatTypeEnum.ADD.type)){
                        log.info("监听到添加BGM事件");
                        //下载bgm到前台
                        URL url = new URL(bgmUrl);
                        File file = new File(localPath);
                        FileUtils.copyURLToFile(url, file);
                        client.delete().forPath(path);
                    } else if(opType.equals(BgmOperatTypeEnum.DELETE.type)){
                        log.info("监听到删除BGM事件");
                        File file = new File(localPath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }

                }
            }
        });
    }

}

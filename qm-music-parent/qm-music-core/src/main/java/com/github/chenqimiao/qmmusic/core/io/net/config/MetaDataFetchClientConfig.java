package com.github.chenqimiao.qmmusic.core.io.net.config;

import com.github.chenqimiao.qmmusic.core.io.net.client.LastfmApiDataFetchClient;
import com.github.chenqimiao.qmmusic.core.io.net.client.MetaDataFetchClient;
import com.github.chenqimiao.qmmusic.core.io.net.client.MetaDataFetchClientCommander;
import com.github.chenqimiao.qmmusic.core.io.net.client.SpotifyApiDataFetchClient;
import com.github.chenqimiao.qmmusic.core.third.lastfm.LastfmClient;
import com.github.chenqimiao.qmmusic.core.third.spotify.SpotifyClient;
import com.github.chenqimiao.qmmusic.core.util.OrderUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 16:11
 **/
@Configuration
@Slf4j
public class MetaDataFetchClientConfig implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext applicationContext;

    private static List<MetaDataFetchClient> unmodifiedMetaDataFetchClients;



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, MetaDataFetchClient> clients = applicationContext.getBeansOfType(MetaDataFetchClient.class);
        List<MetaDataFetchClient> metaDataFetchClients = Lists.newArrayList();
        for(Map.Entry<String,MetaDataFetchClient> entry : clients.entrySet()){
            try {
                if (entry.getValue() instanceof MetaDataFetchClientCommander) {
                    continue;
                }
                MetaDataFetchClient metaDataFetchClient = entry.getValue();
                metaDataFetchClients.add(metaDataFetchClient);
            }catch (Exception e){
                log.error("注册清理器异常 beanName:[{}]", entry.getKey(), e);
            }
        }

        metaDataFetchClients.sort(Comparator.comparingInt(OrderUtils::getOrderValue));

        unmodifiedMetaDataFetchClients = Collections.unmodifiableList(metaDataFetchClients);
    }


    public static List<MetaDataFetchClient> getMetaDataFetchClients() {
        return unmodifiedMetaDataFetchClients;
    }

    @Bean
    public LastfmApiDataFetchClient lastfmApiDataFetchClient() {

        try {
            Map<String, LastfmClient> beanMap = applicationContext.getBeansOfType(LastfmClient.class);
            if (beanMap.isEmpty()) {
                return null;
            }
            return new LastfmApiDataFetchClient(beanMap.values().iterator().next());

        }catch (Exception e){
            return null;
        }

    }


    @Bean
    public SpotifyApiDataFetchClient spotifyApiDataFetchClient(){
        try {
            Map<String, SpotifyClient> beanMap = applicationContext.getBeansOfType(SpotifyClient.class);
            if (beanMap.isEmpty()) {
                return null;
            }
            return new SpotifyApiDataFetchClient(beanMap.values().iterator().next());

        }catch (Exception e){
            return null;
        }
    }

}

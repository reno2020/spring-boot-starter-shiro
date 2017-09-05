package org.throwable.shiro.support;

import lombok.Data;
import org.throwable.shiro.common.constants.RedisModel;
import org.throwable.shiro.configuration.ShiroProperties;
import org.throwable.shiro.support.cache.AbstractRedisTemplate;
import org.throwable.shiro.support.cache.ClusterRedisTemplate;
import org.throwable.shiro.support.cache.SentinelRedisTemplate;
import org.throwable.shiro.support.cache.SingleRedisTemplate;
import org.throwable.shiro.utils.Asserts;
import redis.clients.jedis.HostAndPort;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 18:50
 */
public abstract class RedisTemplateRegisterAssistant {

    private static final String PREFIX = "[";
    private static final String SINGLE = "Redis";
    private static final String SINGLE_PREFIX = SINGLE + PREFIX;
    private static final String SENTINEL = "Sentinel";
    private static final String SENTINEL_PREFIX = SENTINEL + PREFIX;
    private static final String CLUSTER = "Cluster";
    private static final String CLUSTER_PREFIX = CLUSTER + PREFIX;
    private static final String SUFFIX = "]";

    private static Pattern SENTINEL_PATTERN = Pattern.compile("\\[(.*?)]");
    private static RedisModel redisModelStore;

    public static RedisModel resolveRedisModel(ShiroProperties shiroProperties) {
        String redisNodes = shiroProperties.getRedisNodes();
        Asserts.notEmpty(redisNodes, "RedisNodes to process must not be empty!");
        if (redisNodes.startsWith(SINGLE_PREFIX) && redisNodes.endsWith(SUFFIX)) {
            RedisTemplateRegisterAssistant.redisModelStore = RedisModel.SINGLE;
            return RedisModel.SINGLE;
        }
        if (redisNodes.startsWith(SENTINEL_PREFIX) && redisNodes.endsWith(SUFFIX)) {
            RedisTemplateRegisterAssistant.redisModelStore = RedisModel.SENTINEL;
            return RedisModel.SENTINEL;
        }
        if (redisNodes.startsWith(CLUSTER_PREFIX) && redisNodes.endsWith(SUFFIX)) {
            RedisTemplateRegisterAssistant.redisModelStore = RedisModel.CLUSTER;
            return RedisModel.CLUSTER;
        }
        throw new IllegalArgumentException(String.format("RedisNodes is invalid,please check it!Value = %s." +
                        "The valid format likes this:\n%s;\n%s;\n%s;", redisNodes,
                "(1) single node : Redis[host:port]",
                "(2) sentinel nodes : Sentinel[masterName][host1:port1,host2:port2...]",
                "(3) cluster nodes : Cluster[host1:port1,host2:port2...]"));
    }

    public static AbstractRedisTemplate createRedisTemplate(ShiroProperties shiroProperties) {
        AbstractRedisTemplate redisTemplate = null;
        switch (redisModelStore) {
            case SINGLE:
                SingleRedisProp singleRedisProp = resolveSingleRedisProp(shiroProperties);
                redisTemplate = new SingleRedisTemplate(singleRedisProp.getHost(), singleRedisProp.getPort(), singleRedisProp.getPassword(), shiroProperties);
                break;
            case CLUSTER:
                ClusterRedisProp clusterRedisProp = resolveClusterRedisProp(shiroProperties);
                redisTemplate = new ClusterRedisTemplate(clusterRedisProp.getNodes(), shiroProperties);
                break;
            case SENTINEL:
                SentinelRedisProp sentinelRedisProp = resolveSentinelRedisProp(shiroProperties);
                redisTemplate = new SentinelRedisTemplate(sentinelRedisProp.getSentinels(), sentinelRedisProp.getMasterName(), shiroProperties);
                break;
        }
        return redisTemplate;
    }

    private static ClusterRedisProp resolveClusterRedisProp(ShiroProperties shiroProperties) {
        ClusterRedisProp clusterRedisProp = new ClusterRedisProp();
        String redisNodes = shiroProperties.getRedisNodes();
        String nodes = redisNodes.replace(CLUSTER_PREFIX, "").replace(SUFFIX, "");
        String[] splits = nodes.split(",");
        if (1 < splits.length) {
            throw new IllegalArgumentException("Resolve cluster redis nodes properties failed![RedisNodes] value =" + redisNodes);
        }
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        for (String split : splits) {
            String[] node = split.split(":");
            if (2 != node.length) {
                throw new IllegalArgumentException("Resolve cluster redis nodes properties failed![RedisNodes] value =" + redisNodes);
            }
            hostAndPorts.add(new HostAndPort(node[0], Integer.parseInt(node[1])));
        }
        clusterRedisProp.setNodes(hostAndPorts);
        clusterRedisProp.setShiroProperties(shiroProperties);
        return clusterRedisProp;
    }

    private static SentinelRedisProp resolveSentinelRedisProp(ShiroProperties shiroProperties) {
        SentinelRedisProp sentinelRedisProp = new SentinelRedisProp();
        String redisNodes = shiroProperties.getRedisNodes();
        Matcher matcher = SENTINEL_PATTERN.matcher(redisNodes);
        List<String> results = new ArrayList<>();
        while (matcher.find()) {
            results.add(matcher.group(1));
        }
        if (2 != results.size()) {
            throw new IllegalArgumentException("Resolve sentinel redis nodes properties failed![RedisNodes] value =" + redisNodes);
        }
        String masterName = results.get(0);
        if (masterName.contains(":")) {
            throw new IllegalArgumentException("Resolve sentinel redis nodes properties failed![RedisNodes] value =" + redisNodes);
        }
        String nodeResults = results.get(1);
        String[] nodes = nodeResults.split(",");
        Asserts.notEmpty(Arrays.asList(nodes), "Resolve sentinel redis nodes properties failed![RedisNodes] value =%s", redisNodes);
        Set<String> sentinels = new HashSet<>(nodes.length);
        for (String node : nodes) {
            String[] splits = node.split(":");
            if (2 != splits.length) {
                throw new IllegalArgumentException("Resolve single redis nodes properties failed![RedisNodes] value =" + redisNodes);
            }
            sentinels.add(node);
        }
        sentinelRedisProp.setSentinels(sentinels);
        sentinelRedisProp.setMasterName(masterName);
        sentinelRedisProp.setShiroProperties(shiroProperties);
        return sentinelRedisProp;
    }

    private static SingleRedisProp resolveSingleRedisProp(ShiroProperties shiroProperties) {
        SingleRedisProp singleRedisProp = new SingleRedisProp();
        singleRedisProp.setShiroProperties(shiroProperties);
        singleRedisProp.setPassword(shiroProperties.getRedisPassword());
        String redisNodes = shiroProperties.getRedisNodes();
        String node = redisNodes.replace(SINGLE_PREFIX, "").replace(SUFFIX, "");
        String[] splits = node.split(":");
        if (2 != splits.length) {
            throw new IllegalArgumentException("Resolve single redis node properties failed![RedisNodes] value =" + redisNodes);
        }
        singleRedisProp.setHost(splits[0]);
        singleRedisProp.setPort(Integer.parseInt(splits[1]));
        return singleRedisProp;
    }

    @Data
    private static class ClusterRedisProp {

        private Set<HostAndPort> nodes;
        private ShiroProperties shiroProperties;
    }

    @Data
    private static class SentinelRedisProp {

        private Set<String> sentinels;
        private String masterName;
        private ShiroProperties shiroProperties;
    }

    @Data
    private static class SingleRedisProp {

        private String host;
        private int port;
        private String password;
        private ShiroProperties shiroProperties;
    }
}

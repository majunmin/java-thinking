package com.majm.concurrent.cow;

import lombok.Getter;

import java.util.Objects;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/6 12:44 下午
 * @since
 */
@Getter
// 路由信息
public final class Router {
    private final String ip;
    private final Integer port;
    // 接口名
    private final String iface;

    public Router(String ip, Integer port, String iface) {
        this.ip = ip;
        this.port = port;
        this.iface = iface;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Router router = (Router) o;
        return ip.equals(router.ip) &&
                port.equals(router.port) &&
                iface.equals(router.iface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, iface);
    }
}

package com.me.project.hah.service.impl

import com.me.project.hah.dto.conf.HaConf
import com.me.project.hah.dto.ha.Services
import com.me.project.hah.dto.ha.State
import com.me.project.hah.dto.ha.StateFull
import com.me.project.hah.service.HaService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

import javax.annotation.PostConstruct

import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpMethod.POST

@Service
class HaServiceImpl implements HaService {
    private static final Logger log = LoggerFactory.getLogger(HaServiceImpl.class)

    @Autowired
    private HaConf haConf
    @Autowired
    private RestTemplate restTemplate

    String bashUrl
    String token

    @PostConstruct
    void init() {
        this.bashUrl = haConf.common.url
        this.token = haConf.common.token
    }

    @Override
    List<State> states() {
        def url = "${bashUrl}/api/states"
        def responseType = new ParameterizedTypeReference<List<State>>() {}

        def exchange = restTemplate.exchange(url, GET, new HttpEntity<>(simpleHeaders()), responseType)
        exchange.getBody()
    }


    @Override
    State state(String entityId) {
        def url = "${bashUrl}/api/states/${entityId}"

        def exchange = restTemplate.exchange(url, GET, new HttpEntity<>(simpleHeaders()), State)
        exchange.getBody()
    }

    @Override
    void updateState(State state) {
        def url = "${bashUrl}/api/states/${state.entityId()}"

        def exchange = restTemplate.exchange(url, POST, new HttpEntity<>(state, simpleHeaders()), State)
        exchange.getBody()
    }

    @Override
    void createState(StateFull state) {
        def url = "${bashUrl}/api/states/${state.entityId()}"

        def exchange = restTemplate.exchange(url, POST, new HttpEntity<>(state, simpleHeaders()), State)
        exchange.getBody()
    }

    @Override
    List<Services> services() {
        def url = "${bashUrl}/api/services"
        def responseType = new ParameterizedTypeReference<List<Services>>() {}

        def exchange = restTemplate.exchange(url, GET, new HttpEntity<>(simpleHeaders()), responseType)
        exchange.getBody()
    }

    @Override
    List<State> callService(String domain, String item, Map<String, Object> data) {
        def url = "${bashUrl}/api/services/${domain}/${item}"
        def responseType = new ParameterizedTypeReference<List<State>>() {}

        def exchange = restTemplate.exchange(url, POST, new HttpEntity<>(data, simpleHeaders()), responseType)
        exchange.getBody()
    }

    private LinkedMultiValueMap<String, String> simpleHeaders() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>()
        headers.add("Authorization", "Bearer ${token}" as String)
        headers.add("content-type", "application/json")
        headers
    }
}

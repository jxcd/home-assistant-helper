package com.me.project.hah.dto.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ha")
class HaConf {
    Common common
}

class Common {
    String url
    String urlWs
    String token
}

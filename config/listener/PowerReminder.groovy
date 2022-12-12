import com.me.project.hah.dto.ha.State
import com.me.project.hah.service.HaService
import com.me.project.hah.util.SpringTool
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 充电提醒
 * 设备电量充满/设备电量不足时, 提醒拔掉电源/充电
 */
Logger log = LoggerFactory.getLogger("PowerReminder.groovy")

def conf = [
        "sensor.watch4c_battery_level" : ["name": "Watch  ", "mix": 15, "max": 100],
        "sensor.sm_n9760_battery_level": ["name": "Note10+", "mix": 20, "max": 95],
        "sensor.mi6_battery_level"     : ["name": "Mi6    ", "mix": 30, "max": 100],
        "sensor.xsmax_battery_level"   : ["name": "XsMax  ", "mix": 20, "max": 90]
]

return { State newState, State oldState ->
    def entityId = newState.entityId()

    def power0 = oldState.state() as int
    def power = newState.state() as int
    def bean = entityId.with { conf.get(it) }

    if (power0 == power || bean == null) {
        return
    }

    def msg = null

    if (power < power0) {
        // 电量下降
        def min = (bean.get("min") ?: 0) as int
        if (power0 >= min && power < min) {
            msg = "请给 [${bean.name ?: entityId}] 充电, 当前电量 ${power}%"
        }
    } else {
        // 电量上升
        def max = (bean?.get("max") ?: 100) as int
        if (power0 < max && power >= max) {
            msg = "请给 [${bean.name ?: entityId}] 断电, 当前电量 ${power}%"
        }
    }

    if (msg != null) {
        SpringTool.getBean(HaService).callService("notify", "mobile_app_sm_n9760", Map.of("title", "电量提醒", "message", msg))
        log.info("push msg: {}", msg)
    }


}



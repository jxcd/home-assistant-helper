import com.me.project.hah.service.HaService
import com.me.project.hah.util.SpringTool
import groovy.transform.Field

/**
 * 提醒手表, 手机等设备充电
 */
@Field
HaService haService = SpringTool.getBean(HaService)

def list = [
        message("Watch  ", "sensor.watch4c_battery_level", 80),
        message("Note10+", "sensor.sm_n9760_battery_level", 60),
        message("Mi6    ", "sensor.mi6_battery_level", 40),
        message("XsMax  ", "sensor.xsmax_battery_level", 40)
]

def msg = list.findAll { it -> it != null }.join(System.lineSeparator())
haService.callService("notify", "mobile_app_sm_n9760", Map.of("title", "充电提醒", "message", msg))
println "推送充电提醒成功:"
println msg

String message(String name, String entityId, int threshold) {
    def state = haService.state(entityId)

    def power = state.state() as int
    if (power <= threshold) {
        return "请给 [${name}] 充电, 当前电量 ${power}%"
    }

    return null
}

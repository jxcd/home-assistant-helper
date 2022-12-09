import org.slf4j.Logger
import org.slf4j.LoggerFactory

Logger log = LoggerFactory.getLogger("pc_mem_used_listener.groovy");


{ newState, oldState ->
    log.info "process: ${oldState} --> ${newState}"
}

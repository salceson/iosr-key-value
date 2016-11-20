package iosr.keyvalue.web;

import iosr.keyvalue.statemachine.StoreStateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/store")
class StoreController {

    private final StoreStateMachine storeStateMachine;

    @Autowired
    public StoreController(StoreStateMachine storeStateMachine) {
        this.storeStateMachine = storeStateMachine;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Map<Object, Object> getCompleteStoreState() {
        return storeStateMachine.getStoreCopy();
    }

}

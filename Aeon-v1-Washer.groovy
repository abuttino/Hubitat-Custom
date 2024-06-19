/*
Custom Laundry monitor device for Aeon HEM V1 

*/

metadata {
	definition (name: "Aeon HEM V1 Laundry (Washer)", namespace: "Tony B.", author: "Tony B.") 
	{
		capability "Configuration"
		capability "Switch"
        capability "PushableButton"
		capability "Power Meter"
        //capability "Energy Meter"

        attribute "washerWatts", "string
        attribute "washerState", "string"
        
        command "configure"
        
		fingerprint deviceId: "0x2101", inClusters: " 0x70,0x31,0x72,0x86,0x32,0x80,0x85,0x60"
	}

	preferences {
       	input name: "washerRW", type: "number", title: "Washer running watts:", description: "", required: true
        input("debug", "bool", title: "Enable logging?", required: true, defaultValue: false)
       
    }
	
    simulator {

	}

	tiles(scale: 2) {
    	multiAttributeTile(name:"laundryState", type: "generic", width: 6, height: 4){
        	tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
            	attributeState "on", label:'Running', icon:"st.Appliances.appliances1", backgroundColor:"#53a7c0"
            	attributeState "off", label:'Nothing running', icon:"st.Appliances.appliances1", backgroundColor:"#ffffff"
        	}
        }
        valueTile("washerState", "device.washerState", width: 3, height: 2) {
        	state("on", label:'Washer:\nRunning', backgroundColor:"#53a7c0")
            state("off", label:'Washer:\nNot running', backgroundColor:"#ffffff")
        }
        valueTile("washer", "device.washerWatts", width: 3, height: 1, decoration: "flat") {
            state("default", label:'Washer:\n${currentValue} Watts', foregroundColor: "#000000")
        }
		standardTile("configure", "command.configure", inactiveLabel: false) {
			state "configure", label:'', action: "configure", icon:"st.secondary.configure"
		}
		main "laundryState"
		details(["laundryState","washerState","washer","configure"])
	}
}

def installed() {
	configure()					
}

def parse(String description) {
	def result = null
	def cmd = zwave.parse(description, [0x31: 1, 0x32: 1, 0x60: 3])
	if (cmd) {
		result = createEvent(zwaveEvent(cmd))
	}
	if (result) { 
		if (debug) log.debug "Parse returned ${result?.descriptionText}"
		return result
	} else {
	}
}

def zwaveEvent(hubitat.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
	if (debug) log.info "mc3v cmd: ${cmd}"
	if (cmd.commandClass == 50) {  
    def cmd2 = cmd.encapsulatedCommand([0x31: 1, 0x32: 1, 0x60: 3])
    	def encapsulatedCommand = cmd.encapsulatedCommand([0x30: 1, 0x31: 1, 0x60: 3])
      //log.debug "cmd 2 = $cmd2"
      if (encapsulatedCommand) {
      		def endpt = cmd.sourceEndPoint
        	def scale = encapsulatedCommand.scale
        	def value = encapsulatedCommand.scaledMeterValue
            def source = cmd.sourceEndPoint
            def mt = encapsulatedCommand.meterType
           if (debug) log.debug "mt = $mt value = $value, scale = $scale, source = $source, endpt = $endpt"
			def str = ""
            def name = ""
            def desc = ""
        	if (scale == 2 ){ //watts
            	str = "watts"
                if (source == 1){
              //  log.debug "in washer case"
                	name = "washerWatts"
                    desc = "Washer power is " + value + " Watts"
                    if (value >= settings.washerRW.toInteger()){
                    	//washer is on
                        if (debug) log.debug "Washer turned on"
                        sendEvent(name: "washerState", value: "on", displayed: false)
                        state.washerIsRunning = true
                    } else {
                    	//washer is off
                        if (state.washerIsRunning == true)
                        {
                        	//button event
                            if (debug) log.debug "Washer turned off"
                            sendEvent(name: "button", value: "pushed", data: [buttonNumber: 1], descriptionText: "Washer has finished.", isStateChange: true)
                        }
                        sendEvent(name: "washerState", value: "off", displayed: false)
                        state.washerIsRunning = false
                    }
                }
                if (state.washerIsRunning)
                {
                    if (debug) log.debug "Washer has started"
                	sendEvent(name: "switch", value: "on", descriptionText: "Washer has started...", displayed: true)
                }
                    if (debug) log.debug "Global switch turned off"
                	sendEvent(name: "switch", value: "off", displayed: false)
                }
                //log.debug "mc3v- name: ${name}, value: ${value}, unit: ${str}"
                
            	return [name: name, value: value.toInteger(), unit: str, displayed: true, descriptionText: desc]
            }
        }
    }
}

def zwaveEvent(hubitat.zwave.Command cmd) {
	// Handles all Z-Wave commands we aren't interested in
    //log.debug "Unhandled event ${cmd}"
	[:]
}

def configure() {
    log.debug "configure() v2"
    
	def cmd = delayBetween([
    	//zwave.configurationV1.configurationSet(parameterNumber: 100, size: 4, scaledConfigurationValue:1).format(),	//reset if not 0
        //zwave.configurationV1.configurationSet(parameterNumber: 110, size: 4, scaledConfigurationValue: 1).format(),	//reset if not 0
//        	zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 6912).format(), 
//zwave.configurationV1.configurationSet(parameterNumber: 255, size: 4, scaledConfigurationValue: 1).format(), 
// was above.. not sure what that is only want watts ie 512
	
  //  zwave.configurationV1.configurationSet(parameterNumber: 100, size: 4, scaledConfigurationValue:1).format(),	//reset if not 0
   //     zwave.configurationV1.configurationSet(parameterNumber: 110, size: 4, scaledConfigurationValue: 1).format(),	//reset if not 0
    	zwave.configurationV1.configurationSet(parameterNumber: 1, size: 2, scaledConfigurationValue: 120).format(),		// assumed voltage
		zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: 0).format(),			// Disable (=0) selective reporting
		zwave.configurationV1.configurationSet(parameterNumber: 9, size: 1, scaledConfigurationValue: 10).format(),			// Or by 10% (L1)
      	zwave.configurationV1.configurationSet(parameterNumber: 10, size: 1, scaledConfigurationValue: 10).format(),			// Or by 10% (L2)
		zwave.configurationV1.configurationSet(parameterNumber: 20, size: 1, scaledConfigurationValue: 1).format(),			//usb = 1
        zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 512).format(),  
        zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 256).format(),
        zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: 0).format(),
		zwave.configuratonV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: 60).format(), 		// Every 2 minutes seconds
        zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: 120).format() 
	], 2000)

	return cmd
}
definition(
	name: "Water Heater Control",
	namespace: "bspranger",
	author: "Brian Spranger",
	description: "Control Water Heater Setpoint and Operational Modes",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
	page(name: "main")
}

def main()
{
	dynamicPage(name: "main", title: "Water Heater Control", uninstall: true, install: true)
	{
		section 
		{
			input "enabled", "bool", title: "Enable Water Heater Control?", required: false, defaultValue: true
		}
		section("Settings") 
		{
				input "thermostatDevices", "capability.thermostat", title: "Thermostat Devices", multiple: false, required: true
				input "Wake", "time", title: "Time to start Wake", required: true
				input "WakeTemp", "number", required: true, title: "Wake Temperature"
				input "Leave", "time", title: "Time to start Leave", required: true
				input "LeaveTemp", "number", required: true, title: "Leave Temperature"
				input "Return", "time", title: "Time to start Return", required: true
				input "ReturnTemp", "number", required: true, title: "Return Temperature"
				input "Sleep", "time", title: "Time to start Sleep", required: true
				input "SleepTemp", "number", required: true, title: "Sleep Temperature"
				input "logEnable", "bool", title: "Enable debug logging", required: false
				input "mode1", "capability.locationMode", title: "Select a Mode"
		}
	}
}

def installed() {
	unsubscribe()
	initialize()
}


def updated() {
	unsubscribe()
	initialize()
}


def initialize() {
	subscribe(location, modeEvent)
	schedule(Wake, wakeTemp)
	schedule(Leave, leaveTemp)
	schedule(Return, returnTemp)
	schedule(Sleep, sleepTemp)
}

def wakeTemp()
{
	if(enabled) 
	{
		currentTemp = thermostatDevice.currentValue("heatingSetpoint")
		currentMode = thermostatDevice.currentValue("thermostatMode") 
		if (logEnable) 
			log.debug "Thermostat: thermostatDevice Temp: $currentTemp Mode: $currentMode"
		if (currentTemp != WakeTemp)
			thermostatDevice.setHeatingSetpoint(WakeTemp)
	}
}

def leaveTemp()
{
	if(enabled) 
	{
		currentTemp = thermostatDevice.currentValue("heatingSetpoint")
		currentMode = thermostatDevice.currentValue("thermostatMode") 
		if (logEnable) 
			log.debug "Thermostat: thermostatDevice Temp: $currentTemp Mode: $currentMode"
		if (currentTemp != LeaveTemp)
			thermostatDevice.setHeatingSetpoint(LeaveTemp)
	}
}

def returnTemp()
{
	if(enabled) 
	{
		currentTemp = thermostatDevice.currentValue("heatingSetpoint")
		currentMode = thermostatDevice.currentValue("thermostatMode") 
		if (logEnable) 
			log.debug "Thermostat: thermostatDevice Temp: $currentTemp Mode: $currentMode"
		if (currentTemp != ReturnTemp)
			thermostatDevice.setHeatingSetpoint(ReturnTemp)
	}
}

def sleepTemp()
{
	if(enabled) 
	{
		currentTemp = thermostatDevice.currentValue("heatingSetpoint")
		currentMode = thermostatDevice.currentValue("thermostatMode") 
		if (logEnable) 
			log.debug "Thermostat: thermostatDevice Temp: $currentTemp Mode: $currentMode"
		if (currentTemp != SleepTemp)
			thermostatDevice.setHeatingSetpoint(SleepTemp)
	}
}

def modeEvent(evt){
	if(evt.name != "mode") 
		return
	
	if(enabled) 
	{
		currentMode = thermostatDevice.currentValue("thermostatMode")
		
		if (logEnable)
		{
			log.debug "Name: Mode, value: ${evt.value}, Thermostat: $thermostatDevice, Mode:$currentMode"
		}
		
		if ((evt.value == "Day") || (evt.value == "Babysitter"))
		{
			if (currentMode != "Heat Pump Only")
				thermostatDevice.RequestHeatPumpOnly()
		}
		else if ((evt.value == "Night") || (evt.value == "Away"))
		{
			if (currentMode != "Off")
				thermostatDevice.RequestOff()
		}
		else
		{
			
		}
	}
}
	
def uninstalled() {
	unsubscribe()
	removeChildDevices(getChildDevices())
}

private removeChildDevices(delete) {
	delete.each {deleteChildDevice(it.deviceNetworkId)}
}
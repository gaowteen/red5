/*
 * application.js - a translation into JavaScript of the olfa demo application, a red5 example.
 *
 * @author Paul Gregoire
 */

var javaNames = JavaImporter();

javaNames.importPackage(Packages.org.red5.server.adapter);
javaNames.importPackage(Packages.org.red5.server.api);
javaNames.importPackage(Packages.org.red5.server.api.stream);
javaNames.importPackage(Packages.org.red5.server.api.stream.support);

Function.prototype.method = function (name, func) {
    this.prototype[name] = func;
    return this;
};

function Application() {

	var appScope;
	var serverStream;

	with(javaNames) {

		var adapter = new ApplicationAdapter();
	
		//public boolean appStart(IScope app) 
		Application.method('appStart', function (app)  {
			print('Javascript appStart');
			this.appScope = app;
			return true;
		});
		
		//public boolean appConnect(IConnection conn, Object[] params) {
		Application.method('appConnect', function (conn, params)  {
			print('Javascript appConnect');
			adapter.measureBandwidth(conn);
			if (conn == typeof(IStreamCapableConnection)) {
				var streamConn = conn;
				var sbc = new SimpleBandwidthConfigure();
				sbc.setMaxBurst(8*1024*1024);
				sbc.setBurst(8*1024*1024);
				sbc.setOverallBandwidth(2*1024*1024);
				streamConn.setBandwidthConfigure(sbc);
			}
			return adapter.appConnect(conn, params);
		});
		
		//public void appDisconnect(IConnection conn) 
		Application.method('appDisconnect', function (conn)  {
			print('Javascript appDisconnect');
			if (this.appScope == conn.getScope() && serverStream)  {
				serverStream.close();
			}
			return adapter.appDisconnect(conn);
		});
		
		Application.method('toString', function () {
			print('Javascript toString');
			return adapter.toString();
		});
	
	}

}

print('Javascript Application for olfa demo instanced');

with (javaNames) {
    try {
    	var supr = new Application();
    	print('testing js app: ' + supr.appStart(null));
    } catch(e) {
    	print('Exception: ' + e);
    }
}



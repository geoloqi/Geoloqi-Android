package com.geoloqi.rpc;

class ExpiredTokenException extends RPCException {
	ExpiredTokenException() {
		super("Expired token.");
	}
}

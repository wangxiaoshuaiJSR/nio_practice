package com.self.code.provider;


import com.code.self.api.IRpcService;

public class RpcServiceImpl implements IRpcService {

	public int add(int a, int b) {
		return a + b;
	}

	public int sub(int a, int b) {
		return a - b;
	}

	public int mult(int a, int b) {
		return a * b;
	}

	public int div(int a, int b) {
		return a / b;
	}

}

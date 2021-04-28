// IIPCService.aidl
package com.midfang.ipc;

// Declare any non-default types here with import statements

import com.midfang.ipc.model.Request;
import com.midfang.ipc.model.Response;

interface IIPCService {

    Response send(in Request request);

}
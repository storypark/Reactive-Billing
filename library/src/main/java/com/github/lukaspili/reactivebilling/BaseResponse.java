package com.github.lukaspili.reactivebilling;

/*package*/ abstract class BaseResponse {
    public final int responseCode;

    /*package*/ BaseResponse(int responseCode) {
        this.responseCode = responseCode;
    }
}

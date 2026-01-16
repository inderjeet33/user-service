package com.prerana.userservice.exceptions;

public class RejectionReasonMissingException extends BaseException{

    public RejectionReasonMissingException(String errorCode,String message){
        super(errorCode,message);
    }

    public RejectionReasonMissingException(String message){
            super("Rejection Reason Missing",message);
    }
}


package gg.mmorealms.module.core.common.dto;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnLambda;

public record CustomDump(String title, ReturnLambda<String> dumper) {

}

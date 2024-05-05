package com.pkg.littlewriter.domain.generativeAi.openAi;

import com.mysql.cj.x.protobuf.MysqlxCursor;

public interface OpenAiResponse {
   String getMessage() throws OpenAiException;
}

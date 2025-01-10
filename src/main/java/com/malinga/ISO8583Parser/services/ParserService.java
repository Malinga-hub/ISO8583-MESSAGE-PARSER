package com.malinga.ISO8583Parser.services;

import com.malinga.ISO8583Parser.config.SpecConfig;
import com.malinga.ISO8583Parser.dtos.IsoDto;
import com.malinga.ISO8583Parser.dtos.IsoSpecDto;
import com.malinga.ISO8583Parser.utils.ParserUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParserService {
    private final Logger logger;
    private final SpecConfig specConfig;

    public IsoDto parseMsg()
    {
        IsoDto isoDto = new IsoDto();
        try{
            String hexdump = "49 53 4f 38 35 38 33 2d 31 39 39 33 30 31 31 30 30 35 35 30 30\n" +
                    "11 00 f2 34 a7 51 a8 e0  9a 00 00 00 00 00 00 00   \n" +
                    "00 01 10 42 60 92 50 00  69 71 64 00 00 00 00 00   \n" +
                    "00 00 00 01 11 19 12 00  02 29 96 87 24 11 19 12   \n" +
                    "59 21 27 02 11 19 08 94  41 31 30 31 30 31 37 31   \n" +
                    "33 33 34 34 00 00 01 00  55 41 24 11 19 06 00 00   \n" +
                    "36 06 90 37 39 25 04 26  09 25 00 06 97 16 4d 27   \n" +
                    "02 22 60 00 00 00 84 10  00 34 33 32 34 31 32 32   \n" +
                    "39 39 36 38 37 38 39 34  50 30 32 33 39 38 39 34   \n" +
                    "36 30 30 30 30 30 32 36  35 35 36 37 28 54 4f 54   \n" +
                    "41 4c 20 4e 4f 52 54 48  4d 45 41 44 20 20 20 20   \n" +
                    "20 20 20 20 4c 55 53 41  4b 41 20 20 20 20 20 20   \n" +
                    "20 20 20 5a 4d 09 67 4b  9d f6 b1 b2 73 89 f4 08   \n" +
                    "20 01 00 01 00 6d 9f 26  08 ba f7 21 36 71 ff 13   \n" +
                    "fd 5f 2a 02 09 67 9c 01  00 9f 09 00 9a 03 24 11   \n" +
                    "19 9f 37 04 34 3e 12 b7  82 02 00 20 9f 36 02 00   \n" +
                    "0d 9f 35 00 9f 34 00 9f  33 03 e0 08 08 95 05 00   \n" +
                    "00 00 00 00 9f 03 00 9f  1a 02 08 94 9f 02 06 00   \n" +
                    "00 00 00 00 01 9f 10 17  06 01 12 03 a0 00 00 0f   \n" +
                    "03 00 00 00 00 00 00 00  00 00 00 4d b5 32 7b 9f   \n" +
                    "27 01 80 00 75 de 1d 32  20 20 20  ";

            //functional flow
            /* NOTE:
             * all values set with fixed start index and end index are referenced from the ISO 8583 specification document in code repo
             * **/
            isoDto.setHexDump(hexdump);
            ParserUtils.formatHexDump(isoDto.getHexDump(), isoDto);
            ParserUtils.setMessageHeaderData(specConfig, isoDto.getHexDump(), isoDto);
            ParserUtils.setMti(specConfig, isoDto.getHexDump(), isoDto);
            ParserUtils.setBitMap(specConfig, isoDto.getHexDump(), isoDto);
            ParserUtils.processDataElements(specConfig, isoDto.getBitMapDto().getDataElementsPresent(), isoDto.getHexDump(), isoDto);

            logger.info("data: {}", isoDto);
        }
        catch (Exception ex){logger.error("error: {}", ex.toString());}

        return isoDto;
    }
}

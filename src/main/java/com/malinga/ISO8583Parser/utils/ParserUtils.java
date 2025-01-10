package com.malinga.ISO8583Parser.utils;

import com.malinga.ISO8583Parser.config.SpecConfig;
import com.malinga.ISO8583Parser.dtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParserUtils {
    private static final Logger logger = LoggerFactory.getLogger(ParserUtils.class);
    private static final Object[] DATA_STORE = new Object[2];

    public static void formatHexDump(String hexdump, IsoDto isoDto) {
        //remove new line chars, spaces in-between chars and any trailing or leading space
        isoDto.setHexDump(hexdump.replaceAll("\\n", "")
                .replaceAll("\\s", "")
                .trim());
    }

    public static void setMessageHeaderData(SpecConfig specConfig, String hexdump, IsoDto isoDto) {
        try {
            //format msg header
            IsoSpecDto messageHeaderDto = specConfig.getIsoSpecDefinition().stream()
                    .filter(m -> m.getFieldName().equalsIgnoreCase("MESSAGE_HEADER"))
                    .findFirst().orElseThrow(() -> new Exception("MESSAGE_HEADER is NULL"));

            isoDto.setMessageHeaderDto(new MessageHeaderDto());
            Object[] processedData = getDataElementAndMoveHexDumpStartIndex(0, messageHeaderDto.getFieldLength(), "", messageHeaderDto.getFieldType(), hexdump, isoDto);
            String messageHeader = processedData[0].toString();
            isoDto = (IsoDto) processedData[1];

            //set message header values
            messageHeader = hexToAscii(messageHeader);
            String protocolVersionIdentifier = getSubDataElement(0, 12, messageHeader);
            String messageSource = getSubDataElement(12, 14, messageHeader);
            String versionNumber = getSubDataElement(14, 16, messageHeader);
            String fieldInError = getSubDataElement(16, 19, messageHeader);
            String notUsed = getSubDataElement(19, messageHeader.length(), messageHeader);

            //set fields
            isoDto.getMessageHeaderDto().setMessageHeader(messageHeader);
            isoDto.getMessageHeaderDto().setProtocolVersionIdentifier(protocolVersionIdentifier);
            isoDto.getMessageHeaderDto().setMessageSource(messageSource);
            isoDto.getMessageHeaderDto().setVersionNumber(versionNumber);
            isoDto.getMessageHeaderDto().setFieldInError(fieldInError);
            isoDto.getMessageHeaderDto().setNotUsed(notUsed);
        } catch (Exception ex) {
            logger.error("error: {}", ex.toString());
        }
    }

    public static void setMti(SpecConfig specConfig, String hexDump, IsoDto isoDto) {
        try {
            //get mti
            IsoSpecDto mtiDto = specConfig.getIsoSpecDefinition().stream()
                    .filter(m -> m.getFieldName().equalsIgnoreCase("MTI"))
                    .findFirst().orElseThrow(() -> new Exception("MTI IS NULL"));

            isoDto.setMtiDto(new MtiDto());
            Object[] processedData = getDataElementAndMoveHexDumpStartIndex(0, mtiDto.getFieldLength(), "", mtiDto.getFieldType(), hexDump, isoDto);
            String mti = processedData[0].toString();
            isoDto = (IsoDto) processedData[1];


            for (int i = 0; i < mti.length(); i++) {
                char mtiChar = mti.charAt(i);
                if (i == 0) {
                    switch (mtiChar) {
                        case '1':
                            isoDto.getMtiDto().setVersionNumber("ISO 8583: 1993");
                            break;
                        case '9':
                            isoDto.getMtiDto().setVersionNumber("Format error response");
                            break;
                    }
                } else if (i == 1) {
                    switch (mtiChar) {
                        case '1':
                            isoDto.getMtiDto().setMessageClass("Authorisation");
                            break;
                        case '2':
                            isoDto.getMtiDto().setMessageClass("Financial");
                            break;
                        case '4':
                            isoDto.getMtiDto().setMessageClass("Reversal");
                            break;
                        case '8':
                            isoDto.getMtiDto().setMessageClass("Network Management");
                            break;
                    }
                } else if (i == 2) {
                    switch (mtiChar) {
                        case '0':
                            isoDto.getMtiDto().setMessageFunction("Request");
                            break;
                        case '1':
                            isoDto.getMtiDto().setMessageFunction("Request Response");
                            break;
                        case '2':
                            isoDto.getMtiDto().setMessageFunction("Advice");
                            break;
                        case '3':
                            isoDto.getMtiDto().setMessageFunction("Advice Response");
                            break;
                    }
                } else if (i == 3) {
                    switch (mtiChar) {
                        case '0':
                            isoDto.getMtiDto().setTransactionOriginator("Acquirer");
                            break;
                        case '1':
                            isoDto.getMtiDto().setTransactionOriginator("Acquirer Repeat");
                            break;
                        case '2':
                            isoDto.getMtiDto().setTransactionOriginator("Card issuer");
                            break;
                        case '3':
                            isoDto.getMtiDto().setTransactionOriginator("Card issuer repeat");
                            break;
                        case '4':
                            isoDto.getMtiDto().setTransactionOriginator("Other");
                            break;
                        case '5':
                            isoDto.getMtiDto().setTransactionOriginator("Other repeat");
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("error: {}", ex.toString());
        }
    }

    public static void setBitMap(SpecConfig specConfig, String hexDump, IsoDto isoDto) {
        try {
            IsoSpecDto bitMapDto = specConfig.getIsoSpecDefinition().stream()
                    .filter(m -> m.getFieldName().equalsIgnoreCase("BITMAP"))
                    .findFirst().orElseThrow(() -> new Exception("BITMAP IS NULL"));

            isoDto.setBitMapDto(new BitMapDto());
            int count = hexToBCD(String.valueOf(hexDump.charAt(0))).startsWith("1") ? 2 : 1; //check if secondary bitmap present
            for (int i = 0; i < count; i++) {
                //for each bit map found get data elements and update bitmap dto
                Object[] processedData = getDataElementAndMoveHexDumpStartIndex(0, bitMapDto.getFieldLength(), "", bitMapDto.getFieldType(), isoDto.getHexDump(), isoDto);
                String bitmap = processedData[0].toString();
                IsoDto isoDtoInner = (IsoDto) processedData[1];

                if (i == 0) {
                    isoDto.getBitMapDto().setPrimaryBitMap(bitmap);
                } else {
                    isoDto.getBitMapDto().setSecondaryBitMap(bitmap);
                    isoDto.getBitMapDto().setSecondaryBitMapPresent(true);
                }
                logger.info("current hexdump: {}", isoDtoInner.getHexDump());
                isoDto.setHexDump(isoDtoInner.getHexDump());
            }
            //set full bitmap
            isoDto.getBitMapDto().setBitmap(isoDto.getBitMapDto().getPrimaryBitMap() + isoDto.getBitMapDto().getSecondaryBitMap());

            //set bitmap binary representation
            isoDto.getBitMapDto().setBitmapBinaryBCD(hexToBCD(isoDto.getBitMapDto().getBitmap()));

            //set data elements present
            List<Integer> dataElements = new ArrayList<>();
            for (int i = 1; i < isoDto.getBitMapDto().getBitmapBinaryBCD().length(); i++) {
                if (!String.valueOf(isoDto.getBitMapDto().getBitmapBinaryBCD().charAt(i)).equals("1"))
                    continue;
                dataElements.add(i + 1);
            }
            isoDto.getBitMapDto().setDataElementsPresent(dataElements);

        } catch (Exception ex) {
            logger.error("error: {}", ex.toString());
        }
    }

    public static void processDataElements(SpecConfig specConfig, List<Integer> dataElementsPresent, String hexDump, IsoDto isoDto) {
        List<IsoSpecDto> dataElements = new ArrayList<>();
        logger.info("hexdump data elements: {}", hexDump);
        for (Integer dataElement : dataElementsPresent) {
            try {
                IsoSpecDto isoSpecDto = specConfig.getIsoSpecDefinition().stream()
                        .filter(m -> m.getFieldNumber() == dataElement)
                        .findFirst().orElseThrow(() -> new Exception("Data element config not found. FIELD_" + dataElement));

                int skipIndex = 0;
                int fieldLength = isoSpecDto.getFieldLength();
                String tagLength = "";
                if (isoSpecDto.getFieldLengthType().startsWith("variable")) {
                    skipIndex = isoSpecDto.getFieldType().endsWith("lllvar")
                            ? 4 : 2;
                    tagLength = isoDto.getHexDump().substring(0, skipIndex);
                    fieldLength = hexToDecimal(tagLength);
                }
                logger.info("processing field: {} | tag length: {} | field length: {}", isoSpecDto.getFieldName(), tagLength, fieldLength);
                Object[] processedData = getDataElementAndMoveHexDumpStartIndex(0, fieldLength, tagLength,
                        isoSpecDto.getFieldType(), isoDto.getHexDump(), isoDto);

                String result = processedData[0].toString();
                isoDto = (IsoDto) processedData[1];
                result = convertValueToAsciiCheck(isoSpecDto.getFieldType(), result);

                isoSpecDto.setFieldValue(result);
                dataElements.add(isoSpecDto);


            } catch (Exception ex) {
                logger.error("Error: {}", ex.toString());
            }

            isoDto.setDataElements(dataElements);

        }
        logger.info("processed {} data elements", isoDto.getDataElements());
    }

    /*
    class helper functions
     */
    private static Object[] getDataElementAndMoveHexDumpStartIndex(int startIndex, int endIndex, String tagLengthValue,
                                                                   String fieldType, String hexdump, IsoDto isoDto) {
        try {
            switch (fieldType) {
                case "an":
                case "ans":
                case "llvar":
                case "ansllvar":
                    endIndex *= 2;
                    break;
                case "lllvar":
                case "anslllvar":
                case "blllvar":
                    endIndex *= 4;
                    break;
            }
            String result = hexdump.substring(startIndex, endIndex+tagLengthValue.length());
            result = result.substring(tagLengthValue.length());
            result = result.length() % 2 != 0 && result.startsWith("0") ? hexdump.substring(tagLengthValue.length(), endIndex + 1) : result; //get next byte for values starting with zero - avoid left pad of zero
            hexdump = hexdump.substring(result.length()+tagLengthValue.length());

            logger.info("hexdump remaining: {}", hexdump);

            isoDto.setHexDump(hexdump);
            result = padData(result);

            DATA_STORE[0] = result;
            DATA_STORE[1] = isoDto;
        } catch (Exception ex) {
            logger.error("error: {}", ex.toString());
        } finally {
            logger.info("data set: {}", Arrays.toString(DATA_STORE));
        }
        return DATA_STORE;
    }

    private static String getSubDataElement(int startIndex, int endIndex, String dataElement) {
        String result = "";
        try {
            result = dataElement.substring(startIndex, endIndex);
        } catch (Exception ex) {
            logger.error("error: {}", ex.toString());
        } finally {
            logger.info("data-element: {} | sub-data-element: {}", dataElement, result);
        }
        return result;
    }

    private static String hexToAscii(String hexString) {
        StringBuilder result = new StringBuilder();
        try {
            /*
            convert each byte to character representation
            each byte is represented by two BCD(binary coded decimal) characters in hex string
            49534f383538332d31393933303131303035353030 - first byte = 0x49
             */
            for (int i = 0; i < hexString.length(); i += 2) {
                String hexValue = hexString.substring(i, i + 2);
                result.append((char) Integer.parseInt(hexValue, 16));
            }
        } catch (Exception ex) {
            logger.error("error: {}", ex.toString());
        } finally {
            logger.info("ascii value: {}", result.toString());
        }

        return result.toString();
    }

    private static String hexToBCD(String hexString) {
        StringBuilder result = new StringBuilder();
        try {
            for (int i = 0; i < hexString.length(); i++) {
                char charValue = hexString.charAt(i);
                int charInt = Character.digit(charValue, 16); //get numeric representation of char in hex
                String bcd = String.format("%04d", Integer.parseInt(Integer.toBinaryString(charInt))); //convert to bcd string with 4 digits
                result.append(bcd);
            }
        } catch (Exception ex) {
            logger.error("error: {}", ex.toString());
        } finally {
            logger.info("bcd value: {}", result.toString());
        }
        return result.toString();
    }

    public static Integer hexToDecimal(String hexValue) {
        return Integer.parseInt(hexValue, 16);
    }

    private static String padData(String data) {
        /*
        ensure data.length() is even
        i.e. if data = 200 -> return 0200 by padding the data with 0 at the beginning of the string
         */
        if (!data.startsWith("0")) {
            while (data.length() % 2 != 0) {
                data = "0" + data;
            }
        }
        logger.info("padded data: {}", data);
        return data;
    }

    private static String convertValueToAsciiCheck(String fieldType, String fieldValue) {

        if (fieldType.contains("an"))
        {
            fieldValue = hexToAscii(fieldValue);
        }

        return fieldValue;
    }
}

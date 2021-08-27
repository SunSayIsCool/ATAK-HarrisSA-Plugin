package com.atakmap.android.harrissaspr.converters;

public class MILSTDconverter {

    public String milstd_conv_id(int input_domain, int input_unit){
        String cot_domain = null;
        String cot_unit = null;
        switch (input_domain) {
            case 0:
                cot_domain = "a-f-A-M";
                switch (input_unit) {
                    case 0:
                        cot_unit = "-F";
                        break;
                    case 1:
                        cot_unit = "-F-A";
                        break;
                    case 2:
                        cot_unit = "-F-B";
                        break;
                    case 3:
                        cot_unit = "-F-C";
                        break;
                    case 4:
                        cot_unit = "-F-D";
                        break;
                    case 5:
                        cot_unit = "-F-F";
                        break;
                    case 6:
                        cot_unit = "-F-F-I";
                        break;
                    case 7:
                        cot_unit = "-F-H";
                        break;
                    case 8:
                        cot_unit = "-F-J";
                        break;
                    case 9:
                        cot_unit = "-F-K";
                        break;
                    case 10:
                        cot_unit = "-F-L";
                        break;
                    case 11:
                        cot_unit = "-F-M";
                        break;
                    case 12:
                        cot_unit = "-F-O";
                        break;
                    case 13:
                        cot_unit = "-F-P";
                        break;
                    case 14:
                        cot_unit = "-F-Q";
                        break;
                    case 15:
                        cot_unit = "-F-R";
                        break;
                    case 16:
                        cot_unit = "-F-T";
                        break;
                    case 17:
                        cot_unit = "-F-U";
                        break;
                    case 18:
                        cot_unit = "-F-Y";
                        break;
                    case 19:
                        cot_unit = "-H";
                        break;
                    case 20:
                        cot_unit = "-H-A";
                        break;
                    case 21:
                        cot_unit = "-H-C";
                        break;
                    case 22:
                        cot_unit = "-H-D";
                        break;
                    case 23:
                        cot_unit = "-H-H";
                        break;
                    case 24:
                        cot_unit = "-H-J";
                        break;
                    case 25:
                        cot_unit = "-H-M";
                        break;
                    case 26:
                        cot_unit = "-H-O";
                        break;
                    case 27:
                        cot_unit = "-H-Q";
                        break;
                    case 28:
                        cot_unit = "-H-R";
                        break;
                    case 29:
                        cot_unit = "-H-U";
                        break;
                    case 30:
                        cot_unit = "-L";
                        break;
                }
                break;

            case 1:
                cot_domain = "a-f-G-E-V";
                switch (input_unit) {
                    case 0:
                        cot_unit = "";
                        break;
                    case 1:
                        cot_unit = "-A";
                        break;
                    case 2:
                        cot_unit = "-A-A";
                        break;
                    case 3:
                        cot_unit = "-A-A-R";
                        break;
                    case 4:
                        cot_unit = "-A-C";
                        break;
                    case 5:
                        cot_unit = "-A-I";
                        break;
                    case 6:
                        cot_unit = "-A-L";
                        break;
                    case 7:
                        cot_unit = "-A-S";
                        break;
                    case 8:
                        cot_unit = "-A-T";
                        break;
                    case 9:
                        cot_unit = "-C";
                        break;
                    case 10:
                        cot_unit = "-E";
                        break;
                    case 11:
                        cot_unit = "-E-A";
                        break;
                    case 12:
                        cot_unit = "-U";
                        break;
                    case 13:
                        cot_unit = "-U-B";
                        break;
                    case 14:
                        cot_unit = "-U-X";
                        break;
                    case 15:
                        cot_unit = "-U-R";
                        break;
                    case 16:
                        cot_unit = "-U-S";
                        break;
                    case 17:
                        cot_unit = "-m";
                        break;
                }
                break;

            case 2:
                cot_domain = "a-f-G-E-W";
                switch (input_unit) {
                    case 0:
                        cot_unit = "";
                        break;
                    case 1:
                        cot_unit = "-A";
                        break;
                    case 2:
                        cot_unit = "-D";
                        break;
                    case 3:
                        cot_unit = "-G";
                        break;
                    case 4:
                        cot_unit = "-H";
                        break;
                    case 5:
                        cot_unit = "-M";
                        break;
                    case 6:
                        cot_unit = "-O";
                        break;
                    case 7:
                        cot_unit = "-R-R";
                        break;
                    case 8:
                        cot_unit = "-R";
                        break;
                    case 9:
                        cot_unit = "-R-L";
                        break;
                    case 10:
                        cot_unit = "-R-H";
                        break;
                    case 11:
                        cot_unit = "-S";
                        break;
                    case 12:
                        cot_unit = "-X";
                        break;
                    case 13:
                        cot_unit = "-T";
                        break;
                    case 14:
                        cot_domain = "a-f-G-E";
                        cot_unit = "-X-F";
                        break;
                    case 15:
                        cot_domain = "a-f-G-E";
                        cot_unit = "-X-N";
                        break;
                }
                break;

            case 3:
                cot_domain = "a-f-G-U-C";
                switch (input_unit) {
                    case 0:
                        cot_unit = "";
                        break;
                    case 1:
                        cot_unit = "-A-A";
                        break;
                    case 2:
                        cot_unit = "-A-A-A";
                        break;
                    case 3:
                        cot_unit = "-A-A-A-S";
                        break;
                    case 4:
                        cot_unit = "-A-A-A-T";
                        break;
                    case 5:
                        cot_unit = "-A-A-A-W";
                        break;
                    case 6:
                        cot_unit = "-A-A-M";
                        break;
                    case 7:
                        cot_unit = "-A-A-O";
                        break;
                    case 8:
                        cot_unit = "-A-T";
                        break;
                    case 9:
                        cot_unit = "-A-T-A";
                        break;
                    case 10:
                        cot_unit = "-A-W";
                        break;
                    case 11:
                        cot_unit = "-A-W-A";
                        break;
                    case 12:
                        cot_unit = "-A-W-R";
                        break;
                    case 13:
                        cot_unit = "-A-W-W";
                        break;
                    case 14:
                        cot_unit = "-D";
                        break;
                    case 15:
                        cot_unit = "-D-M";
                        break;
                    case 16:
                        cot_unit = "-E";
                        break;
                    case 17:
                        cot_unit = "-F";
                        break;
                    case 18:
                        cot_unit = "-F-M";
                        break;
                    case 19:
                        cot_unit = "-F-R";
                        break;
                    case 20:
                        cot_unit = "-F-R-S-S";
                        break;
                    case 21:
                        cot_unit = "-F-R-M-S";
                        break;
                    case 22:
                        cot_unit = "-I";
                        break;
                    case 23:
                        cot_unit = "-I-A";
                        break;
                    case 24:
                        cot_unit = "-I-I";
                        break;
                    case 25:
                        cot_unit = "-I-M";
                        break;
                    case 26:
                        cot_unit = "-I-N";
                        break;
                    case 27:
                        cot_unit = "-I-S";
                        break;
                    case 28:
                        cot_unit = "-I-O";
                        break;
                    case 29:
                        cot_unit = "-I-Z";
                        break;
                    case 30:
                        cot_unit = "-M";
                        break;
                    case 31:
                        cot_unit = "-R";
                        break;
                    case 32:
                        cot_unit = "-R-A";
                        break;
                    case 33:
                        cot_unit = "-R-O";
                        break;
                    case 34:
                        cot_unit = "-R-R";
                        break;
                    case 35:
                        cot_unit = "-R-S";
                        break;
                    case 36:
                        cot_unit = "-S";
                        break;
                    case 37:
                        cot_unit = "-V";
                        break;
                    case 38:
                        cot_unit = "-V-C";
                        break;
                    case 39:
                        cot_unit = "-V-F";
                        break;
                    case 40:
                        cot_unit = "-V-R";
                        break;
                }
                break;

            case 4:
                cot_domain = "a-f-G-U-U";
                switch (input_unit) {
                    case 0:
                        cot_unit = "";
                        break;
                    case 1:
                        cot_unit = "-A";
                        break;
                    case 2:
                        cot_unit = "-A-B";
                        break;
                    case 3:
                        cot_unit = "-A-C";
                        break;
                    case 4:
                        cot_unit = "-A-N";
                        break;
                    case 5:
                        cot_unit = "-E";
                        break;
                    case 6:
                        cot_unit = "-I";
                        break;
                    case 7:
                        cot_unit = "-L";
                        break;
                    case 8:
                        cot_unit = "-L-C";
                        break;
                    case 9:
                        cot_unit = "-L-M";
                        break;
                    case 10:
                        cot_unit = "-M";
                        break;
                    case 11:
                        cot_unit = "-M-C";
                        break;
                    case 12:
                        cot_unit = "-M-S";
                        break;
                    case 13:
                        cot_unit = "-M-S-E";
                        break;
                    case 14:
                        cot_unit = "-S";
                        break;
                    case 15:
                        cot_unit = "-S-O";
                        break;
                    case 16:
                        cot_unit = "-S-R";
                        break;
                    case 17:
                        cot_unit = "-S-R-S";
                        break;
                    case 18:
                        cot_unit = "-S-R-W";
                        break;
                    case 19:
                        cot_unit = "-S-W";
                        break;
                }
                break;

            case 5:
                cot_domain = "a-f-G-U-S";
                switch (input_unit) {
                    case 0:
                        cot_domain = "a-f-G-U-H";
                        cot_unit = "";
                        break;
                    case 1:
                        cot_unit = "-M";
                        break;
                    case 2:
                        cot_unit = "-S";
                        break;
                    case 3:
                        cot_unit = "-T";
                        break;
                    case 4:
                        cot_unit = "-X";
                        break;
                    case 5:
                        cot_unit = "-X-R";
                        break;
                }
                break;

            case 6:
                cot_domain = "a-f-S";
                switch (input_unit) {
                    case 0:
                        cot_unit = "";
                        break;
                    case 1:
                        cot_unit = "-C";
                        break;
                    case 2:
                        cot_unit = "-C-A";
                        break;
                    case 3:
                        cot_unit = "-C-A-L-A";
                        break;
                    case 4:
                        cot_unit = "-C-A-L-C";
                        break;
                    case 5:
                        cot_unit = "-C-A-L-S";
                        break;
                    case 6:
                        cot_unit = "-C-H";
                        break;
                    case 7:
                        cot_unit = "-C-L";
                        break;
                    case 8:
                        cot_unit = "-C-L-B-B";
                        break;
                    case 9:
                        cot_unit = "-C-L-C-C";
                        break;
                    case 10:
                        cot_unit = "-C-L-C-V";
                        break;
                    case 11:
                        cot_unit = "-C-L-D-D";
                        break;
                    case 12:
                        cot_unit = "-C-L-F-F";
                        break;
                    case 13:
                        cot_unit = "-C-M";
                        break;
                    case 14:
                        cot_unit = "-C-M-M-H";
                        break;
                    case 15:
                        cot_unit = "-C-M-M-L";
                        break;
                    case 16:
                        cot_unit = "-C-P";
                        break;
                    case 17:
                        cot_unit = "-C-P-S-B";
                        break;
                    case 18:
                        cot_unit = "-C-P-S-U";
                        break;
                    case 19:
                        cot_unit = "-G-C";
                        break;
                    case 20:
                        cot_unit = "-N-M";
                        break;
                    case 21:
                        cot_unit = "-N-N-R";
                        break;
                    case 22:
                        cot_unit = "-X";
                        break;
                    case 23:
                        cot_unit = "-X-M-C";
                        break;
                    case 24:
                        cot_unit = "-X-M-P";
                        break;
                    case 25:
                        cot_unit = "-X-M-O";
                        break;
                }
                break;

            case 7:
                cot_domain = "a-f-U";
                switch (input_unit) {
                    case 0:
                        cot_unit = "";
                        break;
                    case 1:
                        cot_unit = "-N-D";
                        break;
                    case 2:
                        cot_unit = "-S";
                        break;
                    case 3:
                        cot_unit = "-S-C";
                        break;
                    case 4:
                        cot_unit = "-S-N";
                        break;
                    case 5:
                        cot_unit = "-S-U";
                        break;
                }
                break;

            case 8:
                cot_domain = "a-f-F";
                switch (input_unit) {
                    case 0:
                        cot_unit = "-A-F";
                        break;
                    case 1:
                        cot_unit = "-A-H";
                        break;
                    case 2:
                        cot_unit = "-A-H-H";
                        break;
                    case 3:
                        cot_unit = "-N";
                        break;
                    case 4:
                        cot_unit = "-N-B";
                        break;
                    case 5:
                        cot_unit = "-N-N";
                        break;
                    case 6:
                        cot_unit = "-G";
                        break;
                    case 7:
                        cot_unit = "-G-R";
                        break;
                    case 8:
                        cot_unit = "-G-P";
                        break;
                    case 9:
                        cot_unit = "-B";
                        break;
                }
                break;

            default:
                cot_domain = "a-f-G-U";
                break;
        }

        return (cot_domain+cot_unit);
    }
}
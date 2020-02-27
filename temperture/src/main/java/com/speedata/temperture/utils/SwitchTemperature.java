package com.speedata.temperture.utils;

import com.speedata.utils.PlaySoundPool;

/**
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                            O\ = /O
 * //                        ____/`---'\____
 * //                      .   ' \\| |// `.
 * //                       / \\||| : |||// \
 * //                     / _||||| -:- |||||- \
 * //                       | | \\\ - /// | |
 * //                     | \_| ''\---/'' | |
 * //                      \ .-\__ `-` ___/-. /
 * //                   ___`. .' /--.--\ `. . __
 * //                ."" '< `.___\_<|>_/___.' >'"".
 * //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //                 \ \ `-. \_ __\ /__ _/ .-` / /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //
 * //         .............................................
 * //                  佛祖镇楼                  BUG辟易
 *
 * @author :EchoXBR in  2020-02-22 16:57.
 * 功能描述:额头温度与实际体温对照表
 */
public class SwitchTemperature {


    public static String switchTemp(String data) {
        String result = data;
//        float temp = Float.parseFloat(data);
//        if (temp < 29) {
//            playSoundPool.playError();
//            return "体温低";
//        } else if (temp > 39) {
//            playSoundPool.playError();
//            return "高温警告";
//        }
        switch (data) {
            case "29.0":
            case "29.1":
                result ="32.0";
                break;
            case "29.2":
            case "29.3":
                result ="33.5";
                break;
            case "29.4":
            case "29.5":
                result ="34.6";
                break;
            case "29.6":
            case "29.7":
                result ="34.7";
                break;
            case "29.8":
            case "29.9":
                result ="34.8";
                break;
            case "30.0":
            case "30.1":
                result ="35.0";
                break;
            case "30.2":
            case "30.3":
                result ="35.3";
                break;
            case "30.4":
            case "30.5":
                result ="35.5";
                break;
            case "30.6":
            case "30.7":
                result ="36.0";
                break;
            case "30.8":
            case "30.9":
                result ="36.0";
                break;
            case "31.0":
                result = "36.0";
                break;
            case "31.1":
            case "31.2":
                result = "36.1";
                break;
            case "31.3":
            case "31.4":
                result = "36.1";
                break;
            case "31.5":
            case "31.6":
                result = "36.1";
                break;
            case "31.7":
            case "31.8":
                result = "36.2";
                break;
            case "31.9":
            case "32.0":
                result = "36.2";
                break;
            case "32.1":
            case "32.2":
                result = "36.3";
                break;
            case "32.3":
            case "32.4":
                result = "36.3";
                break;
            case "32.5":
            case "32.6":
                result = "36.4";
                break;
            case "32.7":
            case "32.8":
                result = "36.4";
                break;
            case "32.9":
            case "33.0":
                result = "36.4";
                break;
            case "33.1":
            case "33.2":
                result = "36.4";
                break;
            case "33.3":
            case "33.4":
                result = "36.4";
                break;
            case "33.5":
            case "33.6":
                result = "36.4";
                break;
            case "33.7":
            case "33.8":
                result = "36.4";
                break;
            case "33.9":
            case "34.0":
                result = "36.5";
                break;
            case "34.1":
            case "34.2":
                result = "36.5";
                break;
            case "34.3":
            case "34.4":
                result = "36.5";
                break;
            case "34.5":
                result = "36.5";
                break;
            case "34.6":
                result = "36.6";
                break;
            case "34.7":
                result = "36.7";
                break;
            case "34.8":
                result = "36.8";
                break;
            case "34.9":
                result = "36.9";
                break;
            case "35.0":
                result = "37.0";
                break;
            case "35.1":
                result = "37.1";
                break;
            case "35.2":
                result = "37.2";
                break;
            case "35.3":
                result = "37.3";
                break;
            case "35.4":
                result = "37.4";
                break;
            case "35.5":
                result = "37.5";
                break;
            case "35.6":
                result = "37.6";
                break;
            case "35.7":
                result = "37.7";
                break;
            case "35.8":
                result = "37.8";
                break;
            case "35.9":
                result = "37.9";
                break;
            case "36.0":
                result = "38.0";
                break;
            case "36.1":
                result = "38.1";
                break;
            case "36.2":
                result = "38.2";
                break;
            case "36.3":
                result = "38.3";
                break;
            case "36.4":
                result = "38.4";
                break;
            case "36.5":
                result = "38.5";
                break;
            case "36.6":
                result = "38.6";
                break;
            case "36.7":
                result = "38.7";
                break;
            case "36.8":
                result = "38.8";
                break;
            case "36.9":
                result = "38.9";
                break;
            case "37.0":
                result = "39.0";
                break;
            case "37.1":
                result = "39.1";
                break;
            case "37.2":
                result = "39.2";
                break;
            case "37.3":
                result = "39.3";
                break;
            case "37.4":
                result = "39.4";
                break;
            case "37.5":
                result = "39.5";
                break;
            case "37.6":
                result = "39.6";
                break;
            case "37.7":
                result = "39.7";
                break;
            case "37.8":
                result = "39.8";
                break;
            case "37.9":
                result = "39.9";
                break;
            case "38.0":
                result = "40.0";
                break;
            case "38.1":
                result = "40.1";
                break;
            case "38.2":
                result = "40.2";
                break;
            case "38.3":
                result = "40.3";
                break;
            case "38.4":
                result = "40.4";
                break;
            case "38.5":
                result = "40.5";
                break;
            case "38.6":
                result = "40.6";
                break;
            case "38.7":
                result = "40.7";
                break;
            case "38.8":
                result = "40.8";
                break;
            case "38.9":
                result = "40.9";
                break;
            case "39.0":
                result = "41.0";
                break;
            default:
                break;

        }
        return result;
    }
}

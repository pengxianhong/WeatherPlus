package com.pengxh.app.weatherplus.bean;

import java.util.List;

public class CityBean {

    /**
     * code : 10000
     * charge : false
     * msg : 查询成功
     * result : {"status":0,"msg":"ok","result":[{"cityid":1,"parentid":0,"citycode":"101010100","city":"北京"}]}
     */

    private String code;
    private boolean charge;
    private String msg;
    private ResultBeanX result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBeanX getResult() {
        return result;
    }

    public void setResult(ResultBeanX result) {
        this.result = result;
    }

    public static class ResultBeanX {
        /**
         * status : 0
         * msg : ok
         * result : [{"cityid":1,"parentid":0,"citycode":"101010100","city":"北京"}]
         */

        private int status;
        private String msg;
        private List<ResultBean> result;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        public static class ResultBean {
            /**
             * cityid : 1
             * parentid : 0
             * citycode : 101010100
             * city : 北京
             */

            private int cityid;
            private int parentid;
            private String citycode;
            private String city;

            public int getCityid() {
                return cityid;
            }

            public void setCityid(int cityid) {
                this.cityid = cityid;
            }

            public int getParentid() {
                return parentid;
            }

            public void setParentid(int parentid) {
                this.parentid = parentid;
            }

            public String getCitycode() {
                return citycode;
            }

            public void setCitycode(String citycode) {
                this.citycode = citycode;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }
        }
    }
}
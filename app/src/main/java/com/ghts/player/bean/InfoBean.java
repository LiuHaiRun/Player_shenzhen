package com.ghts.player.bean;

import java.util.List;

public class InfoBean {

    private List<TrainFLPageBean> trainFLPage;

    public List<TrainFLPageBean> getTrainFLPage() {
        return trainFLPage;
    }

    public void setTrainFLPage(List<TrainFLPageBean> trainFLPage) {
        this.trainFLPage = trainFLPage;
    }

    public static class TrainFLPageBean {
        private List<TrainFLInfoBean> trainFLInfo;

        public List<TrainFLInfoBean> getTrainFLInfo() {
            return trainFLInfo;
        }

        public void setTrainFLInfo(List<TrainFLInfoBean> trainFLInfo) {
            this.trainFLInfo = trainFLInfo;
        }

        public static class TrainFLInfoBean {
            /**
             * tipCh : 开往
             * tipEn : To :
             * destCh : 苹果园
             * destEn : PingGuoYuan
             * fristCh : 首班
             * fristEn : First Train
             * lastCh : 末班
             * lastEn : Last Train
             * firstTime : 06:25
             * lastTime : 23:31
             */

            private String tipCh;
            private String tipEn;
            private String destCh;
            private String destEn;
            private String fristCh;
            private String fristEn;
            private String lastCh;
            private String lastEn;
            private String firstTime;
            private String lastTime;

            public String getTipCh() {
                return tipCh;
            }

            public void setTipCh(String tipCh) {
                this.tipCh = tipCh;
            }

            public String getTipEn() {
                return tipEn;
            }

            public void setTipEn(String tipEn) {
                this.tipEn = tipEn;
            }

            public String getDestCh() {
                return destCh;
            }

            public void setDestCh(String destCh) {
                this.destCh = destCh;
            }

            public String getDestEn() {
                return destEn;
            }

            public void setDestEn(String destEn) {
                this.destEn = destEn;
            }

            public String getFristCh() {
                return fristCh;
            }

            public void setFristCh(String fristCh) {
                this.fristCh = fristCh;
            }

            public String getFristEn() {
                return fristEn;
            }

            public void setFristEn(String fristEn) {
                this.fristEn = fristEn;
            }

            public String getLastCh() {
                return lastCh;
            }

            public void setLastCh(String lastCh) {
                this.lastCh = lastCh;
            }

            public String getLastEn() {
                return lastEn;
            }

            public void setLastEn(String lastEn) {
                this.lastEn = lastEn;
            }

            public String getFirstTime() {
                return firstTime;
            }

            public void setFirstTime(String firstTime) {
                this.firstTime = firstTime;
            }

            public String getLastTime() {
                return lastTime;
            }

            public void setLastTime(String lastTime) {
                this.lastTime = lastTime;
            }
        }
    }
}

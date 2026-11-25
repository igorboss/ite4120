package ee.taltech.ite4120.parking.api;

import ee.taltech.ite4120.parking.model.ParkingApplication;
import java.util.List;

public class ParkingApplicationListResponse {

    private final List<ParkingApplication> data;
    private final Meta meta;

    public ParkingApplicationListResponse(
            List<ParkingApplication> data,
            int limit,
            int offset,
            int total) {
        this.data = data;
        this.meta = new Meta(limit, offset, total);
    }

    public List<ParkingApplication> getData() { return data; }
    public Meta getMeta() { return meta; }

    public static class Meta {
        private final int limit;
        private final int offset;
        private final int total;

        public Meta(int limit, int offset, int total) {
            this.limit = limit;
            this.offset = offset;
            this.total = total;
        }

        public int getLimit() { return limit; }
        public int getOffset() { return offset; }
        public int getTotal() { return total; }
    }
}

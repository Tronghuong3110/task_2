package com.newlife.Connect_multiple.entity.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

//@Entity
@Data
@Document(collection = "probe")
public class DatabaseOfServer {
    @Id
    private String _id;
    private Boolean disabled;
    private String province_id;
    private String net_x;
    private String url;
    private String type;
    private String created_time;
    private String creator_id;
    private String last_modified_time;
    private String last_modifier_id;
    private InfoDatabaseConnect db_connection;
    private InfoRequestServer request_server_info;
    private Location loc;
    private NormalizedColumns columns;
    private NormalizedColumns normalized_columns;
}

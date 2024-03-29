package es.thalesalv.chatrpg.adapters.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BumpEntity {

    public String role;
    public String content;
    public Integer frequency;
}

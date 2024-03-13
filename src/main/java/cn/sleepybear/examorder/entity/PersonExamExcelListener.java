package cn.sleepybear.examorder.entity;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/07 16:47
 */
@Getter
public class PersonExamExcelListener<T> implements ReadListener<T> {
    private final List<T> data = new ArrayList<>();

    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        this.data.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

}

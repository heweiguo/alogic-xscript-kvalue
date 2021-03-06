package com.alogic.xscript.kvalue.zset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.kvalue.KVRowOperation;
import com.anysoft.util.Pair;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.SortedSetRow;


/**
 * @author zhongyi
 *
 */
public class KVZRangeByScore extends KVRowOperation {

	protected String min = "0";
	protected String max = "150";
	protected String withscores = "false";
	protected String reverse = "false";
	protected String tag = "data";
	
	
	/**
	 * 预留2个参数用来支持分页
	 */
	protected String offset = "0";
	protected String count = "100";

	public KVZRangeByScore(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		min = PropertiesConstants.getRaw(p, "min", min);
		max = PropertiesConstants.getRaw(p, "max", max);
		withscores = PropertiesConstants.getRaw(p, "withscores", withscores);
		reverse = PropertiesConstants.getRaw(p, "reverse", reverse);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {

		if (row instanceof SortedSetRow) {
			SortedSetRow r = (SortedSetRow) row;
			if(getBoolean(ctx.transform(withscores), false)){
				
				boolean _reverse=getBoolean(ctx.transform(reverse), false);
				List<Pair<String,Double>> l=null;
				if(_reverse){
					l=r.rangeByScoreWithScores(getDouble(ctx.transform(max), 0), getDouble(ctx.transform(min), 150d),
							_reverse);
				}else{
					l=r.rangeByScoreWithScores(getDouble(ctx.transform(min), 0), getDouble(ctx.transform(max), 150d),
							_reverse);
				}
				
							
				List<Map<String,Double>> result=new ArrayList<Map<String,Double>>();
				if(null!=l&&l.size()>0){
					Iterator<Pair<String,Double>> ite=l.iterator();
					while(ite.hasNext()){
						Pair<String,Double> p=ite.next();
						Map<String,Double> map=new HashMap<String,Double>();
						map.put(p.key(), p.value());
						result.add(map);
					}
				}
				
				current.put(ctx.transform(tag), result);
			}else{
				boolean _reverse=getBoolean(ctx.transform(reverse), false);
				List<Pair<String,Double>> l=null;
				if(_reverse){
					current.put(ctx.transform(tag), r.rangeByScore(getDouble(ctx.transform(max), 0), getDouble(ctx.transform(min), 150l),
							_reverse));
				}else{
					current.put(ctx.transform(tag), r.rangeByScore(getDouble(ctx.transform(min), 0), getDouble(ctx.transform(max), 150l),
							_reverse));
				}
		
				
			}
				
		}

	}
}

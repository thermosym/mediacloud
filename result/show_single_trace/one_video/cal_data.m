clear all;
close all;
v = 1000;
slot_interval = 0.1;
task_interval = 5;

run bbb_trans_trace_result_single_lyap_medium.m;
lya_index_task_0 = 1:1:size(lya_task_delay_0,2);
lya_avg_time_delay_0 = cumsum(lya_task_delay_0)./lya_index_task_0;
delay_lyap = lya_avg_time_delay_0(end);

lya_avg_quality_0 = cumsum(lya_task_quality_0)./lya_index_task_0;
quality_lyap = lya_avg_quality_0(end);

run bbb_trans_trace_result_single_static_superfast.m;
sta_index_task_0 = 1:1:size(sta_task_delay_0,2);

sta_avg_time_delay_0 = cumsum(sta_task_delay_0)./sta_index_task_0;
delay_sta(1) = sta_avg_time_delay_0(end);

sta_avg_quality_0 = cumsum(sta_task_quality_0)./sta_index_task_0;
quality_sta(1) = sta_avg_quality_0(end);

run bbb_trans_trace_result_single_static_faster.m;
sta_index_task_0 = 1:1:size(sta_task_delay_0,2);

sta_avg_time_delay_0 = cumsum(sta_task_delay_0)./sta_index_task_0;
delay_sta(2) = sta_avg_time_delay_0(end);

sta_avg_quality_0 = cumsum(sta_task_quality_0)./sta_index_task_0;
quality_sta(2) = sta_avg_quality_0(end);

run bbb_trans_trace_result_single_static_medium.m;
sta_index_task_0 = 1:1:size(sta_task_delay_0,2);

sta_avg_time_delay_0 = cumsum(sta_task_delay_0)./sta_index_task_0;
delay_sta(3) = sta_avg_time_delay_0(end);

sta_avg_quality_0 = cumsum(sta_task_quality_0)./sta_index_task_0;
quality_sta(3) = sta_avg_quality_0(end);

delay_lyap,quality_lyap,delay_sta,quality_sta

clear all;
close all;
run result_avg_static.m;

index=1:1:size(avg_delay_preset(),1);

%%only one video
figure;
plot(index,avg_delay_preset(:,1)','-*');
title('delay');
figure;
plot(index,avg_quality_preset(:,1)','-*');
title('bitrate');
figure;
plot(index,avg_qlen_preset(:,1)','-*');
title('qlen');
figure;
plot(index,avg_qbacklog_preset(:,1)','-*');
title('backlog');

%%two video
close all;
figure;
plot(index,avg_delay_preset(:,1)','-*b');hold on;
plot(index,avg_delay_preset(:,2)','-sr');
title('delay');
figure;
plot(index,avg_quality_preset(:,1)','-*b');hold on;
plot(index,avg_quality_preset(:,2)','-sr');
title('bitrate');
figure;
plot(index,avg_qlen_preset(:,1)','-*b');
title('qlen');
figure;
plot(index,avg_qbacklog_preset(:,1)','-*b');
title('backlog');


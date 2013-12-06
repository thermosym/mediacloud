clear all;close all;
run queueData;
%index = 1:1:length(task_delay);
%indexq = index + 10;
%plot(index_delay,task_delay,'.',index_qlen,task_qlen,'-',index_Sqlen,task_Sqlen,'--');
%legend('delay','Qlen','S-Qlen','Location','NorthWest');

% get the T slot average arrival number
Tslot = index_Sarrival(2) - index_Sarrival(1);
AVGslot = 10;
T = Tslot * AVGslot;
for index=1:1:length(task_Sarrival)
    %get the average
    low_index = max(1,index-AVGslot+1);
    %avg_arrival(index) =  sum(task_Sarrival(low_index:index))/AVGslot;
    avg_arrival(index) =  mean(task_Sarrival(low_index:index));
end
theory_delay = (Tslot./avg_arrival).*task_Sqlen;

%plot(index_delay,task_delay,'.',index_Sqlen,task_Sqlen,'--',index_Sqlen,theory_delay,'.-r');
%plot(index_delay,task_delay,'-*b',index_Sqlen,task_Sqlen,'.-g');
plot(index_delay,task_delay,'-*b',index_delay,task_wsize,'.-g',index_delay,task_svrsize,'sr'); 
legend('delay','Q-WorkSize','VM.number','Location','NorthWest');
title('delay-QworkSize analysis');
xlabel('time (s)','FontSize',20,'FontWeight','bold');
ylabel('delay/Q-WorkSize (s)','FontSize',20,'FontWeight','bold');
% 
% figure;
% plot(index_Sarrival,avg_arrival,'-');
figure;
%plot(index_Slimit,task_Slimit,'-',index_Slimit,task_Spara,'*');
plot(index_Slimit,task_Spara,'s');

figure;
x = histc(task_delay,0:1:10);
bar(x/length(task_delay));
xlabel('delay (s)','FontSize',20,'FontWeight','bold');
ylabel('probability','FontSize',20,'FontWeight','bold');

package com.team14.virtualwallet.repositories;

import com.team14.virtualwallet.models.Transaction;
import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllBySenderAndReceiverAndExecutedOnBetween(UserProfile sender, UserProfile receiver, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Transaction> findAllBySender(UserProfile sender);

    @Query(value = "select *\n" +
            "from transactions as t\n" +
            "join user_profiles as up_r on up_r.user_id = t.receiver_id\n" +
            "join wallets as wallet_receiver on wallet_receiver.id = t.receiver_wallet_id\n" +
            "join user_profiles as up_s on up_s.user_id = t.sender_id\n" +
            "left join wallets as wallet_sender on wallet_sender.id = t.sender_wallet_id\n" +
            "join users as u_s on u_s.id = up_s.user_id\n" +
            "join users as u_r on u_r.id = up_r.user_id\n" +
            "where u_s.username like IF(:senderName = '', '%', :senderName)\n" +
            "and u_r.username like IF(:recipientName = '', '%', :recipientName)\n" +
            "and t.executed_on >= IF(:startDate = '', '1970-01-01', :startDate)\n" +
            "and t.executed_on <= IF(:endDate = '', '2099-12-31', :endDate)" //ORDER BY :#{#pageable}
            , countQuery = "select count(*)\n" +
            "from transactions as t\n" +
            "join user_profiles as up_r on up_r.user_id = t.receiver_id\n" +
            "join wallets as wallet_receiver on wallet_receiver.id = t.receiver_wallet_id\n" +
            "join user_profiles as up_s on up_s.user_id = t.sender_id\n" +
            "left join wallets as wallet_sender on wallet_sender.id = t.sender_wallet_id\n" +
            "join users as u_s on u_s.id = up_s.user_id\n" +
            "join users as u_r on u_r.id = up_r.user_id\n" +
            "where u_s.username like IF(:senderName = '', '%', :senderName)\n" +
            "and u_r.username like IF(:recipientName = '', '%', :recipientName)\n" +
            "and t.executed_on >= IF(:startDate = '', '1970-01-01', :startDate)\n" +
            "and t.executed_on <= IF(:endDate = '', '2099-12-31', :endDate)",
            nativeQuery = true)
    Page<Transaction> findTransactionsAdminPage(String senderName, String recipientName, String startDate, String endDate, Pageable pageable);

    @Query(value = "select *\n" +
            "from transactions as t\n" +
            "join user_profiles as up_r on up_r.user_id = t.receiver_id\n" +
            "join wallets as wallet_receiver on wallet_receiver.id = t.receiver_wallet_id\n" +
            "join user_profiles as up_s on up_s.user_id = t.sender_id\n" +
            "left join wallets as wallet_sender on wallet_sender.id = t.sender_wallet_id\n" +
            "join users as u_s on u_s.id = up_s.user_id\n" +
            "join users as u_r on u_r.id = up_r.user_id\n" +
            "where (u_s.username like IF(:senderName = '', '%', :senderName)\n" +
            "or u_r.username like IF(:recipientName = '', '%', :recipientName)\n)" +
            "and t.executed_on >= IF(:startDate = '', '1970-01-01', :startDate)\n" +
            "and t.executed_on <= IF(:endDate = '', '2099-12-31', :endDate)" //ORDER BY :#{#pageable}
            , countQuery = "select count(*)\n" +
            "from transactions as t\n" +
            "join user_profiles as up_r on up_r.user_id = t.receiver_id\n" +
            "join wallets as wallet_receiver on wallet_receiver.id = t.receiver_wallet_id\n" +
            "join user_profiles as up_s on up_s.user_id = t.sender_id\n" +
            "left join wallets as wallet_sender on wallet_sender.id = t.sender_wallet_id\n" +
            "join users as u_s on u_s.id = up_s.user_id\n" +
            "join users as u_r on u_r.id = up_r.user_id\n" +
            "where (u_s.username like IF(:senderName = '', '%', :senderName)\n" +
            "or u_r.username like IF(:recipientName = '', '%', :recipientName)\n)" +
            "and t.executed_on >= IF(:startDate = '', '1970-01-01', :startDate)\n" +
            "and t.executed_on <= IF(:endDate = '', '2099-12-31', :endDate)",
            nativeQuery = true)
    Page<Transaction> findAllUserTransactions(String senderName, String recipientName, String startDate, String endDate, Pageable pageable);

    @Query(value = "select *\n" +
            "from transactions as t\n" +
            "join user_profiles as up_r on up_r.user_id = t.receiver_id\n" +
            "join user_profiles as up_s on up_s.user_id = t.sender_id\n" +
            "join users as u_s on u_s.id = up_s.user_id\n" +
            "join users as u_r on u_r.id = up_r.user_id\n" +
            "where (u_s.username like IF(:senderName = '', '%', :senderName)\n" +
            "or u_r.username like IF(:senderName = '', '%', :senderName)\n)" +
            "and (u_s.username like IF(:recipientName = '', '%', :recipientName)\n" +
            "or u_r.username like IF(:recipientName = '', '%', :recipientName)\n)" +
            "and t.executed_on >= IF(:startDate = '', '1970-01-01', :startDate)\n" +
            "and t.executed_on <= IF(:endDate = '', '2099-12-31', :endDate)" //ORDER BY :#{#pageable}
            , countQuery = "select count(*)\n" +
            "from transactions as t\n" +
            "join user_profiles as up_r on up_r.user_id = t.receiver_id\n" +
            "join user_profiles as up_s on up_s.user_id = t.sender_id\n" +
            "join users as u_s on u_s.id = up_s.user_id\n" +
            "join users as u_r on u_r.id = up_r.user_id\n" +
            "where (u_s.username like IF(:senderName = '', '%', :senderName)\n" +
            "or u_r.username like IF(:senderName = '', '%', :senderName)\n)" +
            "and (u_s.username like IF(:recipientName = '', '%', :recipientName)\n" +
            "or u_r.username like IF(:recipientName = '', '%', :recipientName)\n)" +
            "and t.executed_on >= IF(:startDate = '', '1970-01-01', :startDate)\n" +
            "and t.executed_on <= IF(:endDate = '', '2099-12-31', :endDate)",
            nativeQuery = true)
    Page<Transaction> findUserTransactionsOfTwoUsers(String senderName, String recipientName, String startDate, String endDate, Pageable pageable);

    @Query(value = "select count(*)\n" +
            "from transactions as t\n" +
            "join user_profiles as up_r on up_r.user_id = t.receiver_id\n" +
            "join wallets as wallet_receiver on wallet_receiver.id = t.receiver_wallet_id\n" +
            "join user_profiles as up_s on up_s.user_id = t.sender_id\n" +
            "left join wallets as wallet_sender on wallet_sender.id = t.sender_wallet_id\n" +
            "join users as u_s on u_s.id = up_s.user_id\n" +
            "join users as u_r on u_r.id = up_r.user_id\n" +
            "where (u_s.username like IF(:senderName = '', '%', :senderName)\n" +
            "or u_r.username like IF(:recipientName = '', '%', :recipientName)\n)" +
            "and t.executed_on >= IF(:startDate = '', '1970-01-01', :startDate)\n" +
            "and t.executed_on <= IF(:endDate = '', '2099-12-31', :endDate)" //ORDER BY :#{#pageable}
//            ,
//            countQuery = "select count(*)\n" +
//            "from transactions as t\n" +
//            "join user_profiles as up_r on up_r.user_id = t.receiver_id\n" +
//            "join user_profiles as up_s on up_s.user_id = t.sender_id\n" +
//            "join users as u_s on u_s.id = up_s.user_id\n" +
//            "join users as u_r on u_r.id = up_r.user_id\n" +
//            "where (u_s.username like IF(:senderName = '', '%', :senderName)\n" +
//            "or u_r.username like IF(:recipientName = '', '%', :recipientName)\n)" +
//            "and t.executed_on >= IF(:startDate = '', '1970-01-01', :startDate)\n" +
//            "and t.executed_on <= IF(:endDate = '', '2099-12-31', :endDate)"
            , nativeQuery = true)
    int findUserTransactionsPerDate(String senderName, String recipientName, String startDate, String endDate);

    @Query("SELECT max(t.executedOn) " +
            "FROM Transaction t " +
            "JOIN UserProfile up_sender on up_sender.id = t.sender.id " +
            "JOIN UserProfile up_receiver on up_receiver.id = t.receiver.id " +
            "LEFT JOIN Wallet w_sender on w_sender.id = t.senderWallet.id " +
            "LEFT JOIN Wallet w_receiver on w_receiver.id = t.receiverWallet.id " +
            "where up_sender = :sender " +
            "and up_receiver = :receiver " +
            "and w_sender= :senderWallet  " +
            "and w_receiver = :receiverWallet " +
            "and t.amount = :amount ")
    LocalDateTime findLastTransaction(UserProfile sender,
                                      Wallet senderWallet,
                                      UserProfile receiver,
                                      Wallet receiverWallet,
                                      BigDecimal amount);


}

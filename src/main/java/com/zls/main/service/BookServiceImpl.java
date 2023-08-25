package com.zls.main.service;

import com.zls.main.dto.BookRobDto;
import com.zls.main.mapper.BookRobMapper;
import com.zls.main.mapper.BookStockMapper;
import com.zls.main.model.BookRob;
import com.zls.main.model.BookStock;
import com.zls.main.model.BookStockExample;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BookServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    @Autowired
    private BookStockMapper bookStockMapper;

    @Autowired
    private BookRobMapper bookRobMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class)
    @Async
    public void rob(BookRobDto dto) throws Exception {
        BookStockExample example = new BookStockExample();
        example.createCriteria().andBookNoEqualTo(dto.getBookNo());

        List<BookStock> list = bookStockMapper.selectByExample(example);
        BookStock stock = null;

        if(list.size() > 0){
            stock = list.get(0);
        }
        int tol = bookRobMapper.countByBookNoUserId(dto.getUserId(), dto.getBookNo());

        if(stock != null && stock.getStock() > 0 && tol < 1){
            log.info("----不加锁，当前信息{}", dto);

            int res = bookStockMapper.updateStock(dto.getBookNo());

            if(res > 0){
                BookRob bookRob = new BookRob();

                BeanUtils.copyProperties(dto, bookRob);
                bookRob.setRobTime(new Date());

                bookRobMapper.insertSelective(bookRob);
            }
        }else {
            log.error("---库存不足，寄---");
            throw new Exception("该库存不足");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Async
    public void roblock(BookRobDto dto) {
        final String lockName = "redissonTryLick-" + dto.getBookNo() + "-" + dto.getUserId();
        RLock lock = redissonClient.getLock(lockName);

        try {
            Boolean rest = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if(rest){
                BookStockExample example = new BookStockExample();
                example.createCriteria().andBookNoEqualTo(dto.getBookNo());

                List<BookStock> list = bookStockMapper.selectByExample(example);
                BookStock stock = null;

                if(list.size() > 0){
                    stock = list.get(0);
                }
                int tol = bookRobMapper.countByBookNoUserId(dto.getUserId(), dto.getBookNo());

                if(stock != null && stock.getStock() > 0 && tol < 1){
                    log.info("----不加锁，当前信息{}", dto);

                    int res = bookStockMapper.updateStock(dto.getBookNo());

                    if(res > 0){
                        BookRob bookRob = new BookRob();

                        BeanUtils.copyProperties(dto, bookRob);
                        bookRob.setRobTime(new Date());

                        bookRobMapper.insertSelective(bookRob);
                    }
                }else {
                    log.error("---库存不足，寄---");
                    throw new Exception("该库存不足");
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(lock!=null){
                lock.unlock();
            }
        }

    }
}

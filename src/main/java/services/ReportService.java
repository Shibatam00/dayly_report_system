package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Report;
import models.validators.ReportValidator;

public class ReportService extends ServiceBase {

    public List<ReportView> getMinePerPage(EmployeeView employee, int page) {

        //指定した従業員の日報一覧リスト
        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_MINE, Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .getResultList();

        return ReportConverter.toViewList(reports);
    }

    //指定した従業員の日報データの件数を取得
    public long countAllMine(EmployeeView employee) {
        long count = (long) em.createNamedQuery(JpaConst.Q_REP_COUNT_ALL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .getSingleResult();

        return count;
    }

    //日報一覧リスト(従業員指定なし)
    public List<ReportView> getAllPerPage(int page) {

        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL, Report.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }

    //日報データ件数(従業員指定なし)
    public long countAll() {
        long reports_count = (long) em.createNamedQuery(JpaConst.Q_REP_COUNT, Long.class).getSingleResult();
        return reports_count;
    }

    //idを条件にデータを取得
    public ReportView findOne(int id) {
        return ReportConverter.toView(findOneInternal(id));
    }

    private Report findOneInternal(int id) {
        return em.find(Report.class, id);
    }

    //日報新規登録
    public List<String> create(ReportView rv) {

        List<String> errors = ReportValidator.validate(rv);
        if (errors.size() == 0) {
            LocalDateTime ldt = LocalDateTime.now();
            rv.setCreatedAt(ldt);
            rv.setUpdatedAt(ldt);
            createInternal(rv);
        }
        return errors;
    }

    //createのデータべースへの保存用
    private void createInternal(ReportView rv) {

        em.getTransaction().begin();
        em.persist(ReportConverter.toModel(rv));
        em.getTransaction().commit();
    }

    public List<String> update(ReportView rv) {
        List<String> errors = ReportValidator.validate(rv);

        if (errors.size() == 0) {
            LocalDateTime ldt = LocalDateTime.now();

            rv.setUpdatedAt(ldt);

            updateInternal(rv);
        }
        return errors;
    }
    //更新用データベース処理
    private void updateInternal(ReportView rv) {

        em.getTransaction().begin();
        Report r = findOneInternal(rv.getId());
        ReportConverter.copyViewToModel(r, rv);
        em.getTransaction().commit();
    }
}

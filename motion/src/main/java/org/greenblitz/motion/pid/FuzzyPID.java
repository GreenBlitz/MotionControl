package org.greenblitz.motion.pid;

import org.greenblitz.motion.fuzzylogic.FuzzyRuleSet;
import org.greenblitz.motion.fuzzylogic.FuzzyValue;
import org.greenblitz.motion.fuzzylogic.IMemFunc;
import org.greenblitz.motion.fuzzylogic.Rule;
import org.greenblitz.motion.tolerance.ITolerance;

import java.util.Arrays;
import java.util.List;

public class FuzzyPID extends PIDController {

    private FuzzyValue NB = new FuzzyValue("NB", new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            return norm(1 - 5 * (normalizedVal + 0.6));
        }
    }, new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            double ret = Math.pow(Math.E, -100 * normalizedVal * normalizedVal);
            if (ret < 0.01) ret = 0;
            return norm(ret);
        }
    });

    private FuzzyValue NM = new FuzzyValue("NM", new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            if (normalizedVal > -0.4) return norm(1 - 5 * (normalizedVal + 0.4));
            return norm(1 + 5 * (normalizedVal + 0.4));
        }
    }, new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            double ret = Math.pow(Math.E, -100 * (normalizedVal - 1 / 6) * (normalizedVal - 1 / 6));
            if (ret < 0.01) ret = 0;
            return norm(ret);
        }
    });

    private FuzzyValue NS = new FuzzyValue("NS", new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            if (normalizedVal > -0.2) return norm(1 - 5 * (normalizedVal + 0.2));
            return norm(1 + 5 * (normalizedVal + 0.2));
        }
    }, new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            double ret = Math.pow(Math.E, -100 * (normalizedVal - 2 / 6) * (normalizedVal - 2 / 6));
            if (ret < 0.01) ret = 0;
            return norm(ret);
        }
    });
    private FuzzyValue ZO = new FuzzyValue("ZO", new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            if (normalizedVal > 0) return norm(1 - 5 * (normalizedVal));
            return norm(1 + 5 * (normalizedVal));
        }
    }, new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            double ret = Math.pow(Math.E, -100 * (normalizedVal - 3 / 6) * (normalizedVal - 3 / 6));
            if (ret < 0.01) ret = 0;
            return norm(ret);
        }
    });
    private FuzzyValue PS = new FuzzyValue("PS", new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            if (normalizedVal > 0.2) return norm(1 - 5 * (normalizedVal - 0.2));
            return norm(1 + 5 * (normalizedVal - 0.2));
        }
    }, new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            double ret = Math.pow(Math.E, -100 * (normalizedVal - 4 / 6) * (normalizedVal - 4 / 6));
            if (ret < 0.01) ret = 0;
            return norm(ret);
        }
    });
    private FuzzyValue PM = new FuzzyValue("PM", new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            if (normalizedVal > 0.4) return norm(1 - 5 * (normalizedVal - 0.4));
            return norm(1 + 5 * (normalizedVal - 0.4));
        }
    }, new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            double ret = Math.pow(Math.E, -100 * (normalizedVal - 5 / 6) * (normalizedVal - 5 / 6));
            if (ret < 0.01) ret = 0;
            return norm(ret);
        }
    });
    private FuzzyValue PB = new FuzzyValue("PB", new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            if (normalizedVal > 0.6) return norm(1 - 5 * (normalizedVal - 0.6));
            return norm(1 + 5 * (normalizedVal - 0.6));
        }
    }, new IMemFunc() {
        @Override
        public double membershipFunction(double normalizedVal) {
            double ret = Math.pow(Math.E, -100 * (normalizedVal - 1) * (normalizedVal - 1));
            if (ret < 0.01) ret = 0;
            return norm(ret);
        }
    });

    List<FuzzyValue> types = Arrays.asList(NB, NM, NS, ZO, PS, PM, PB);

    //e,ce output
    List<Rule> KdRules = Arrays.asList(new Rule(new FuzzyValue[]{NB, NB}, PB),
            new Rule(new FuzzyValue[]{NB, NM}, PS),
            new Rule(new FuzzyValue[]{NB, NS}, NB),
            new Rule(new FuzzyValue[]{NB, ZO}, NB),
            new Rule(new FuzzyValue[]{NB, PS}, NB),
            new Rule(new FuzzyValue[]{NB, PM}, NM),
            new Rule(new FuzzyValue[]{NB, PB}, PB),

            new Rule(new FuzzyValue[]{NM, NB}, PB),
            new Rule(new FuzzyValue[]{NM, NM}, NM),
            new Rule(new FuzzyValue[]{NM, NS}, NB),
            new Rule(new FuzzyValue[]{NM, ZO}, NB),
            new Rule(new FuzzyValue[]{NM, PS}, NM),
            new Rule(new FuzzyValue[]{NM, PM}, NM),
            new Rule(new FuzzyValue[]{NM, PB}, ZO),

            new Rule(new FuzzyValue[]{NS, NB}, PM),
            new Rule(new FuzzyValue[]{NS, NM}, PB),
            new Rule(new FuzzyValue[]{NS, NS}, NM),
            new Rule(new FuzzyValue[]{NS, ZO}, NB),
            new Rule(new FuzzyValue[]{NS, PS}, NS),
            new Rule(new FuzzyValue[]{NS, PM}, PS),
            new Rule(new FuzzyValue[]{NS, PB}, PS),

            new Rule(new FuzzyValue[]{ZO, NB}, ZO),
            new Rule(new FuzzyValue[]{ZO, NM}, NS),
            new Rule(new FuzzyValue[]{ZO, NS}, NS),
            new Rule(new FuzzyValue[]{ZO, ZO}, ZO),
            new Rule(new FuzzyValue[]{ZO, PS}, NS),
            new Rule(new FuzzyValue[]{ZO, PM}, NS),
            new Rule(new FuzzyValue[]{ZO, PB}, ZO),

            new Rule(new FuzzyValue[]{PS, NB}, ZO),
            new Rule(new FuzzyValue[]{PS, NM}, ZO),
            new Rule(new FuzzyValue[]{PS, NS}, ZO),
            new Rule(new FuzzyValue[]{PS, ZO}, ZO),
            new Rule(new FuzzyValue[]{PS, PS}, ZO),
            new Rule(new FuzzyValue[]{PS, PM}, ZO),
            new Rule(new FuzzyValue[]{PS, PB}, ZO),

            new Rule(new FuzzyValue[]{PM, NB}, PS),
            new Rule(new FuzzyValue[]{PM, NM}, NB),
            new Rule(new FuzzyValue[]{PM, NS}, PB),
            new Rule(new FuzzyValue[]{PM, ZO}, PB),
            new Rule(new FuzzyValue[]{PM, PS}, NM),
            new Rule(new FuzzyValue[]{PM, PM}, NS),
            new Rule(new FuzzyValue[]{PM, PB}, NB),

            new Rule(new FuzzyValue[]{PB, NB}, PB),
            new Rule(new FuzzyValue[]{PB, NM}, NS),
            new Rule(new FuzzyValue[]{PB, NS}, NB),
            new Rule(new FuzzyValue[]{PB, ZO}, NB),
            new Rule(new FuzzyValue[]{PB, PS}, NB),
            new Rule(new FuzzyValue[]{PB, PM}, NM),
            new Rule(new FuzzyValue[]{PB, PB}, PB));

    List<Rule> KpRules = Arrays.asList(new Rule(new FuzzyValue[]{NB, NB}, ZO),
            new Rule(new FuzzyValue[]{NB, NM}, ZO),
            new Rule(new FuzzyValue[]{NB, NS}, NS),
            new Rule(new FuzzyValue[]{NB, ZO}, NS),
            new Rule(new FuzzyValue[]{NB, PS}, PS),
            new Rule(new FuzzyValue[]{NB, PM}, ZO),
            new Rule(new FuzzyValue[]{NB, PB}, ZO),

            new Rule(new FuzzyValue[]{NM, NB}, NS),
            new Rule(new FuzzyValue[]{NM, NM}, NS),
            new Rule(new FuzzyValue[]{NM, NS}, NM),
            new Rule(new FuzzyValue[]{NM, ZO}, NM),
            new Rule(new FuzzyValue[]{NM, PS}, NS),
            new Rule(new FuzzyValue[]{NM, PM}, ZO),
            new Rule(new FuzzyValue[]{NM, PB}, NS),

            new Rule(new FuzzyValue[]{NS, NB}, PS),
            new Rule(new FuzzyValue[]{NS, NM}, ZO),
            new Rule(new FuzzyValue[]{NS, NS}, NS),
            new Rule(new FuzzyValue[]{NS, ZO}, PS),
            new Rule(new FuzzyValue[]{NS, PS}, ZO),
            new Rule(new FuzzyValue[]{NS, PM}, ZO),
            new Rule(new FuzzyValue[]{NS, PB}, NS),

            new Rule(new FuzzyValue[]{ZO, NB}, PM),
            new Rule(new FuzzyValue[]{ZO, NM}, PM),
            new Rule(new FuzzyValue[]{ZO, NS}, PS),
            new Rule(new FuzzyValue[]{ZO, ZO}, ZO),
            new Rule(new FuzzyValue[]{ZO, PS}, NS),
            new Rule(new FuzzyValue[]{ZO, PM}, NM),
            new Rule(new FuzzyValue[]{ZO, PB}, NM),

            new Rule(new FuzzyValue[]{PS, NB}, PS),
            new Rule(new FuzzyValue[]{PS, NM}, PS),
            new Rule(new FuzzyValue[]{PS, NS}, ZO),
            new Rule(new FuzzyValue[]{PS, ZO}, NS),
            new Rule(new FuzzyValue[]{PS, PS}, NM),
            new Rule(new FuzzyValue[]{PS, PM}, NM),
            new Rule(new FuzzyValue[]{PS, PB}, NM),

            new Rule(new FuzzyValue[]{PM, NB}, PS),
            new Rule(new FuzzyValue[]{PM, NM}, ZO),
            new Rule(new FuzzyValue[]{PM, NS}, NS),
            new Rule(new FuzzyValue[]{PM, ZO}, NS),
            new Rule(new FuzzyValue[]{PM, PS}, NM),
            new Rule(new FuzzyValue[]{PM, PM}, NM),
            new Rule(new FuzzyValue[]{PM, PB}, NB),

            new Rule(new FuzzyValue[]{PB, NB}, ZO),
            new Rule(new FuzzyValue[]{PB, NM}, ZO),
            new Rule(new FuzzyValue[]{PB, NS}, NM),
            new Rule(new FuzzyValue[]{PB, ZO}, NM),
            new Rule(new FuzzyValue[]{PB, PS}, NB),
            new Rule(new FuzzyValue[]{PB, PM}, NB),
            new Rule(new FuzzyValue[]{PB, PB}, NB));


    FuzzyRuleSet ruleSet = new FuzzyRuleSet(2, types);

    private double MaxError;
    private double MaxErrorChange;
    private double dkp_range;
    private double dkd_range;

    public FuzzyPID(PIDObject obj, ITolerance tolerance, double MaxError, double MaxErrorChange, double dkp_range, double dkd_range) {
        super(obj, tolerance);
        this.MaxError = MaxError;
        this.MaxErrorChange = MaxErrorChange;
        this.dkd_range = dkd_range;
        this.dkp_range = dkp_range;
    }

    private double norm(double x) {
        return Math.max(Math.min(x, 1), 0);
    }

    public double calculatePID(double current) {
        if (!configured)
            throw new RuntimeException("PID - " + this + " - not configured");

        if (isFinished(current))
            return 0;

        double err = m_goal - current;
        double dt = updateTime();

        double dKp = m_obj.getKp() * dkp_range * ruleSet.calculate(new double[]{err / MaxError, (err - m_previousError) / dt / MaxErrorChange}, KpRules);
        double p = (m_obj.getKp() + dKp) * err;

        m_integral += err * dt;
        double i = m_obj.getKi() * m_integral;

        double dKd = m_obj.getKd() * dkd_range * ruleSet.calculate(new double[]{err, (err - m_previousError) / dt}, KdRules);
        double d = (m_obj.getKd() + dKd) * (err - m_previousError) / dt;

        m_previousError = err;
        double calc = clamp(p + i + d + m_obj.getKf());
        return Math.max(Math.abs(calc), m_absoluteMinimumOut) * Math.signum(calc);
    }
}

package jminusminus;

public class JDoStatement extends JStatement {

    private JStatement statement;
    private JExpression expression;

    JDoStatement (int line, JStatement statement, JExpression expression) {
        super(line);
        this.statement = statement;
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {

    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JDoStatement:" + line, e);

        JSONElement body = new JSONElement();
        e.addChild("Body", body);
        statement.toJSON(body);

        JSONElement condition = new JSONElement();
        e.addChild("Condition", condition);
        expression.toJSON(condition);
    }

}

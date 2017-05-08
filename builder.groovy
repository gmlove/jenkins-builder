
class Stages {
    def static stage(opts) {
        if(opts.type == 'b') {
            return new BStage(opts.options)
        } else if(opts.type == 'd') {
            return new DStage(opts.options)
        }
    }

    static class Stage implements Serializable {
        String name

        Stage(java.util.LinkedHashMap opts) {
            name = opts.name
        }
    }
    static class BStage extends Stage {
        String bp

        BStage(java.util.LinkedHashMap opts) {
            super(opts)
            bp = opts.bp
        }

        def call1(env) {
            env.stage('build') {
                env.node('master') {
                    env.sh 'echo build - ' + bp
                }
            }
        }
    }
    static class DStage extends Stage {
        String dp

        DStage(java.util.LinkedHashMap opts) {
            super(opts)
            dp = opts.dp
        }

        def call1(env) {
            env.stage('deploy - ' + dp) {
                env.node('master') {
                    env.sh 'echo deploy - ' + dp
                }
            }
        }
    }


}


def constructPipeline(opts, java.util.Map defopts=[]) {
    opts.putAll(defopts)
    for(stage in opts.stages) {
        Stages.stage(stage).call1(this)
    }
}

return this

constructPipeline([
    project: [
        name: 'test-project',
    ],
    stages: [
        [ type: 'b', options: [bp: 'bp - 1'] ],
        [ type: 'd', options: [dp: 'dp - 1'] ],
        [ type: 'd', options: [dp: 'dp - 2'] ],
        [ type: 'd', options: [dp: 'dp - 3'] ],
    ]
])



class Stage implements Serializable {
    String name

    Stage(java.util.LinkedHashMap opts) {
        name = opts.name
    }
}

class BStage extends Stage {
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

class DStage extends Stage {
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

class Stages {

    def static stage(opts) {
        if(opts.type == 'b') {
            return new BStage(opts.options)
        } else if(opts.type == 'd') {
            return new DStage(opts.options)
        }
    }

}


def constructPipeline(opts, java.util.LinkedHashMap defopts=[]) {
    opts.putAll(defopts)
    for(stage in opts.stages) {
        Stages.stage(stage).call1(this)
    }
}

return this


'''

// TO TEST:
def m(a, b=1, c=2) { "a=$a, b=$b, c=$c" }

stage('test') {
    node('master') {
        sh "echo m.a ${m(1)}"
        sh "echo m.a.b " + m(a=1, b='b1')
        sh "echo m.a.c " + m(a=1, c='c1')
    }
}

builder = fileLoader.fromGit('builder.groovy', 'https://github.com/gmlove/jenkins-builder.git', 'master', null, 'master')

def runner = {env, opts -> env.stage('test') {
    env.sh 'echo customized stage'
}}

builder.constructPipeline([
    project: [
        name: 'test-project',
    ],
    stages: [
        [ type: 'g', options: [runner: runner]],
        [ type: 'b', options: [bp: 'bp - 1'] ],
        [ type: 'd', options: [dp: 'dp - 1'] ],
        [ type: 'd', options: [dp: 'dp - 2'] ],
        [ type: 'd', options: [dp: 'dp - 3'] ],
    ]
])
'''



# IE hacks

do -> Array::filter ?= (callback) ->
	element for element in this when callback element


# HELPERS 
randomInt = (lower, upper=0) ->
	start = Math.random()
	if not lower?
		[lower, upper] = [0, lower]
	if lower > upper
		[lower, upper] = [upper, lower]
	return Math.floor(start * (upper - lower + 1) + lower)


randomDate = (start, end) ->
	return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()))

# CLAIMS
userInfo =
	login: "tadams"
	userId: 100
	firstName: "Terry"
	lastName: "Adams"
	email: "tadams@gmail.com"
	availableApplications: [
			name: "Admin Tool"
			url: "http://localhost/admintool"
		,
			name: "Service Tracker"
			url: "http://localhost/servicetracker"
	]
	permissions: [
		"BestFit.ApproveRefund"
	]


# DICTIONARIES
createBranch= (id) ->
	id: id, name: "Branch #{id}"

createNoteType = (id) ->
	id: id, name: "Note Type #{id}"

createDeclineReason = (id) ->
	id: id, name: "Decline Reason #{id}"

createRule = (id) ->
	id: id, name: "Rule #{id}"

branches = (createBranch(num) for num in [1..50])
noteTypes = (createNoteType(num) for num in [1..50])
declineReasons = (createDeclineReason(num) for num in [1..50])
rules = (createRule(num) for num in [1..50])


# VISITS
createActivityRecord = (id) -> 
  visitId: null
  client:
    branch:
      id: 2
      name: "1050 - Wilmington"
      roundingModulo: 6

    id: 3
    firstName: ["John", "Mark", "Jack", "John", "Mick", "Rick"][randomInt(0,5)]
    lastName: ["Brown", "Grey", "White", "Johnson", "Rickson", "Blunt"][randomInt(0,5)]
    address: "Toreza#{id}"
    phone: "6666666#{id}"

  travelTime: "00:4#{id}:00"
  travelDistance: "5.#{id}"
  errandDistance: 3.4
  visitOutcome: 0
  carePlanTasks: null
  changesInCondition: null
  visitNotes: [{"author": "Jack White","createdAt": "2013-11-1#{id}T09:10:00","text": "comment #{id}"},"author": "Mark Rickson","createdAt": "2013-11-1#{id}T09:10:00","text": "comment #{id}"]
  noSignature: false
  id: id
  startTime: "2013-11-1#{id}T09:10:00"
  startTimeRounded: "2013-11-0#{id}T02:00:00"
  endTime: "2013-11-1#{id}T09:50:00"
  endTimeRounded: "2013-11-0#{id}T03:00:00"
  serviceCode: "PCG"
  source: 0
  employee:
    branch:
      id: 2
      name: "1050 - Wilmington"
      roundingModulo: 6

    isAmpUser: false
    id: 4
    firstName: ["John", "Mark", "Jack", "John", "Mick", "Rick"][randomInt(0,5)]
    lastName: ["Brown", "Grey", "White", "Johnson", "Rickson", "Blunt"][randomInt(0,5)]
    address: "Adr"
    phone: "7777777"

  scheduled:
    startTime: "2013-11-1#{id}T09:00:00"
    endTime: "2013-11-1#{id}T10:00:00"
    duration: "01:00:00"

  validationResults: [
    ruleId: "NoCarePlan"
    success: false
    severity: 0
    message: null
  ,
    ruleId: "NoSchedule"
    success: true
    severity: 0
    message: null
  ,
    ruleId: "NoSignature"
    success: true
    severity: 0
    message: null
  ,
    ruleId: "ShortVisit15"
    success: true
    severity: 0
    message: null
  ,
    ruleId: "ShortVisit30"
    success: true
    severity: 0
    message: null
  ]
  lockedByUserId: id
  lockedByUserName: "Test"
  exported: false
  duration: "00:4#{id}:00"
  durationRounded: "01:0#{id}:00"
  readOnly: false


# CLIENT
createClient = (id) ->
	id: id
	firstName: ["John", "Mark", "Jack", "John", "Mick", "Rick"][randomInt(0,5)]
	lastName: ["Brown", "Grey", "White", "Johnson", "Rickson", "Blunt"][randomInt(0,5)]
	address: ["1#{id} Edison Rd Joliet", "#{id} Edison Rd Joliet", "Mark black #{id}1"][randomInt(0,2)]
	phone: "815-456-65-4#{id}"
	language: ["english", "spanish"][randomInt(0,1)]

# CANDIDATE
createCandidate = (id) ->
	employee:
		id: id
		firstName: ["John", "Mark", "Jack", "John", "Mick", "Rick"][randomInt(0,5)]
		lastName: ["Brown", "Grey", "White", "Johnson", "Rickson", "Blunt"][randomInt(0,5)]
		address: ["1#{id} Edison Rd Joliet", "#{id} Edison Rd Joliet", "Mark black #{id}1"][randomInt(0,2)]
		phone: "815-45#{id}-65-4#{id}"
		language: ["english", "spanish"][randomInt(0,1)]
		skills: ["cook", "clearing"]
		latitude: 23.45
		longitude: 45.34 
		timeFrom: "2013-08-30T0#{[randomInt(1,5)]}:36:45.606Z"
		timeTo: '2013-08-30T09:36:45.606Z'
	score: 10.45
	distance: 15 + randomInt(1,5)
	visitId: randomInt(1,5)
	status: ["D", "N"][randomInt(0,1)]

# COMMENT
createNewComment = (id) ->
	id: id
	createdAt: "201#{randomInt(1,4)}-10-14T09:10:00"
	author: ["John Brown", "Mark Grey", "Jack White", "John Johnson", "Mick Rickson", "Rick Blunt"][randomInt(0,5)]
	text: "Comment #{id}"

visits = (createActivityRecord(num) for num in [1..5])
 
###
[{"visitId":null,"client":{"branch":{"id":2,"name":"1050 - Wilmington","roundingModulo":6},"id":3,"firstName":"Yuriy","lastName":"Kononov","address":"Toreza","phone":"6666666"},"travelTime":"00:45:00","travelDistance":5.8,"errandDistance":3.4,"visitOutcome":0,"carePlanTasks":null,"changesInCondition":null,"visitNotes":[{"id":3,"createdAt":"2013-10-14T09:10:00","author":"Ness, Roger","text":"Comment 1"},{"id":4,"createdAt":"2013-10-15T15:40:00","author":"Damarla, Krishna","text":"Comment 2"},{"id":7,"createdAt":"2013-11-18T08:33:24.36","author":"Test user","text":"test"},{"id":15,"createdAt":"2013-11-19T15:11:36.197","author":"Kirill, Ivanov","text":"comment"}],"noSignature":false,"id":13,"startTime":"2013-11-18T09:10:00","startTimeRounded":"2013-11-07T02:00:00","endTime":"2013-11-18T09:50:00","endTimeRounded":"2013-11-07T03:00:00","serviceCode":"PCG","source":0,"employee":{"branch":{"id":2,"name":"1050 - Wilmington","roundingModulo":6},"isAmpUser":false,"id":4,"firstName":"Michael","lastName":"Chaffee","address":"Adr","phone":"7777777"},"scheduled":{"startTime":"2013-11-18T09:00:00","endTime":"2013-11-18T10:00:00","duration":"01:00:00"},"validationResults":[{"ruleId":"NoCarePlan","success":false,"severity":0,"message":null},{"ruleId":"NoSchedule","success":true,"severity":0,"message":null},{"ruleId":"NoSignature","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit15","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit30","success":true,"severity":0,"message":null}],"lockedByUserId":2,"lockedByUserName":"Kirill Ivanov (HILL30\\kivanov)","exported":false,"duration":"00:40:00","durationRounded":"01:00:00","readOnly":false},{"visitId":null,"client":{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"id":2,"firstName":"Michael","lastName":"Zevin","address":"Abbey RD","phone":"5555555"},"travelTime":"01:00:00","travelDistance":10.0,"errandDistance":5.0,"visitOutcome":0,"carePlanTasks":null,"changesInCondition":null,"visitNotes":[{"id":5,"createdAt":"2013-10-14T09:10:00","author":"Ness, Roger","text":"Comment X"},{"id":6,"createdAt":"2013-10-15T15:40:00","author":"Damarla, Krishna","text":"Comment Y"}],"noSignature":false,"id":14,"startTime":"2013-11-18T15:00:00","startTimeRounded":"2013-11-01T02:00:00","endTime":"2013-11-18T15:10:00","endTimeRounded":"2013-11-01T03:00:00","serviceCode":"PCG","source":0,"employee":{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"isAmpUser":false,"id":3,"firstName":"Krishna","lastName":"Damarla","address":"Adr","phone":"6666666"},"scheduled":{"startTime":"2013-11-18T15:00:00","endTime":"2013-11-18T16:00:00","duration":"01:00:00"},"validationResults":[{"ruleId":"NoCarePlan","success":false,"severity":0,"message":null},{"ruleId":"NoSchedule","success":true,"severity":0,"message":null},{"ruleId":"NoSignature","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit15","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit30","success":false,"severity":0,"message":null}],"lockedByUserId":6,"lockedByUserName":"Michael Feingold (mfeingold)","exported":false,"duration":"00:10:00","durationRounded":"01:00:00","readOnly":false},{"visitId":123456,"client":{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"id":2,"firstName":"Michael","lastName":"Zevin","address":"Abbey RD","phone":"5555555"},"travelTime":"00:45:00","travelDistance":5.8,"errandDistance":3.4,"visitOutcome":0,"carePlanTasks":null,"changesInCondition":null,"visitNotes":[{"id":16,"createdAt":"2013-10-14T09:10:00","author":"Ness, Roger","text":"Comment 1"},{"id":17,"createdAt":"2013-10-15T15:40:00","author":"Damarla, Krishna","text":"Comment 2"}],"noSignature":false,"id":20,"startTime":"2013-11-21T13:10:00","startTimeRounded":"2013-11-21T02:00:00","endTime":"2013-11-21T13:45:00","endTimeRounded":"2013-11-21T03:00:00","serviceCode":"PCG","source":0,"employee":{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"isAmpUser":false,"id":2,"firstName":"Roger","lastName":"Ness","address":"Adr","phone":"5555555"},"scheduled":{"startTime":"2013-11-19T13:00:00","endTime":"2013-11-19T13:45:00","duration":"00:45:00"},"validationResults":[{"ruleId":"NoCarePlan","success":true,"severity":0,"message":null},{"ruleId":"NoSchedule","success":true,"severity":0,"message":null},{"ruleId":"NoSignature","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit15","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit30","success":true,"severity":0,"message":null}],"lockedByUserId":1,"lockedByUserName":"Michael Zevin (HILL30\\mzevin)","exported":false,"duration":"00:35:00","durationRounded":"01:00:00","readOnly":false},{"visitId":null,"client":{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"id":2,"firstName":"Michael","lastName":"Zevin","address":"Abbey RD","phone":"5555555"},"travelTime":"00:45:00","travelDistance":5.8,"errandDistance":3.4,"visitOutcome":0,"carePlanTasks":null,"changesInCondition":null,"visitNotes":[],"noSignature":false,"id":18,"startTime":"2013-11-14T09:10:00","startTimeRounded":"2013-11-14T09:15:00","endTime":"2013-11-14T10:34:14","endTimeRounded":"2013-11-14T10:30:00","serviceCode":"BBB","source":0,"employee":{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"isAmpUser":false,"id":2,"firstName":"Roger","lastName":"Ness","address":"Adr","phone":"5555555"},"scheduled":{"startTime":"2013-11-13T09:00:00","endTime":"2013-11-13T09:00:00","duration":"00:00:00"},"validationResults":[{"ruleId":"NoCarePlan","success":false,"severity":0,"message":null},{"ruleId":"NoSchedule","success":true,"severity":0,"message":null},{"ruleId":"NoSignature","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit15","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit30","success":true,"severity":0,"message":null}],"lockedByUserId":2,"lockedByUserName":"Kirill Ivanov (HILL30\\kivanov)","exported":false,"duration":"01:24:14","durationRounded":"01:15:00","readOnly":false},{"visitId":null,"client":{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"id":2,"firstName":"Michael","lastName":"Zevin","address":"Abbey RD","phone":"5555555"},"travelTime":"23:00:00","travelDistance":7.0,"errandDistance":8.0,"visitOutcome":0,"carePlanTasks":null,"changesInCondition":null,"visitNotes":[{"id":10,"createdAt":"2013-10-14T09:10:00","author":"Ness, Roger","text":"Comment 1"},{"id":11,"createdAt":"2013-10-15T15:40:00","author":"Damarla, Krishna","text":"Comment 2"}],"noSignature":false,"id":16,"startTime":"2013-11-18T09:10:00","startTimeRounded":"2013-11-18T09:15:00","endTime":"2013-11-18T09:15:00","endTimeRounded":"2013-11-18T10:45:00","serviceCode":"PCG","source":0,"employee":{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"isAmpUser":false,"id":2,"firstName":"Roger","lastName":"Ness","address":"Adr","phone":"5555555"},"scheduled":{"startTime":"2013-11-18T09:00:00","endTime":"2013-11-18T10:00:00","duration":"01:00:00"},"validationResults":[{"ruleId":"NoCarePlan","success":false,"severity":0,"message":null},{"ruleId":"NoSchedule","success":true,"severity":0,"message":null},{"ruleId":"NoSignature","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit15","success":false,"severity":0,"message":null},{"ruleId":"ShortVisit30","success":true,"severity":0,"message":null}],"lockedByUserId":2,"lockedByUserName":"Kirill Ivanov (HILL30\\kivanov)","exported":false,"duration":"00:05:00","durationRounded":"01:30:00","readOnly":false},{"visitId":null,"client":{"branch":{"id":2,"name":"1050 - Wilmington","roundingModulo":6},"id":3,"firstName":"Yuriy","lastName":"Kononov","address":"Toreza","phone":"6666666"},"travelTime":"00:45:00","travelDistance":5.8,"errandDistance":3.4,"visitOutcome":0,"carePlanTasks":null,"changesInCondition":null,"visitNotes":[{"id":8,"createdAt":"2013-10-14T09:10:00","author":"Ness, Roger","text":"Comment 1"},{"id":9,"createdAt":"2013-10-15T15:40:00","author":"Damarla, Krishna","text":"Comment 2"}],"noSignature":false,"id":15,"startTime":"2013-11-18T10:00:00","startTimeRounded":"2013-11-18T17:00:00","endTime":"2013-11-18T10:30:00","endTimeRounded":"2013-11-19T10:30:00","serviceCode":"PCG","source":0,"employee":{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"isAmpUser":false,"id":2,"firstName":"Roger","lastName":"Ness","address":"Adr","phone":"5555555"},"scheduled":{"startTime":"2013-11-18T10:00:00","endTime":"2013-11-18T11:00:00","duration":"01:00:00"},"validationResults":[{"ruleId":"NoCarePlan","success":false,"severity":0,"message":null},{"ruleId":"NoSchedule","success":true,"severity":0,"message":null},{"ruleId":"NoSignature","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit15","success":true,"severity":0,"message":null},{"ruleId":"ShortVisit30","success":true,"severity":0,"message":null}],"lockedByUserId":2,"lockedByUserName":"Kirill Ivanov (HILL30\\kivanov)","exported":false,"duration":"00:30:00","durationRounded":"17:30:00","readOnly":false}]
###

clients = (createClient(num) for num in [1..40])
candidates = (createCandidate(num) for num in [1..25])


module.exports = (app, options) ->

	io = require('socket.io').listen(5001)
	io.set('loglevel',10)
	_socket = undefined
	io.sockets.on 'connection', (socket) ->
		_socket = socket
		c = 0
		timer_is_on = true
		_socket.emit 'count', c

		setInterval( ->
			c++
			_socket.emit 'count',  c 
		, 
		10000)

	app.get '/', (req, res) ->
		res.render "#{options.base}/index.html"


	###
	1. GET api/dictionaries

	request params: none
	response params:
	{
		branches: [ { id: <int>, name: <string>} ]
		noteTypes: [ {id: <int>, name: <string>} ]
		declineReasons: [ {id: <int>, name: <string>} ]
	}
	###

	app.get '/api/dictionaries', (req, res) ->

		dictionaries = {
				branches: branches
				noteTypes: noteTypes
				declineReasons: declineReasons
				rules: rules
			}

		res.json {
			"branches":[{"id":1,"name":"320 - MetroSouth","roundingModulo":0},{"id":2,"name":"1050 - Wilmington","roundingModulo":0},{"id":3,"name":"1051 - Milford","roundingModulo":0},{"id":4,"name":"1083 - York","roundingModulo":0}],"validationRuleList":[{"id":"NoSchedule","name":"No Schedule","icon":"glyphicon-calendar"},{"id":"NoSignature","name":"No Signature","icon":"glyphicon-pencil"},{"id":"NoCarePlan","name":"No Care Plan","icon":"glyphicon-list-ul"},{"id":"ShortVisit15","name":"Short Visit 15'","icon":"glyphicon-stopwatch"},{"id":"ShortVisit30","name":"Short Visit 30'","icon":"glyphicon-stopwatch"}],
			"recordSources":[{"id":0,"name":"Console"},{"id":1,"name":"CellTrak"},{"id":2,"name":"SanData"},{"id":3,"name":"CareWatch"},{"id":4,"name":"Amp"},{"id":5,"name":"FirstData"}],			
			"clients": (createClient(num) for num in [1..40])
			"candidates": (createCandidate(num) for num in [1..25])
			"serviceCodes":[{"id":1,"code":"PCG","description":"Code PCG","activityType":"V","hoursBased":true,"liveIn":false},{"id":2,"code":"HCR","description":"Home Care","activityType":"V","hoursBased":true,"liveIn":false},{"id":3,"code":"LI","description":"Live In","activityType":"V","hoursBased":true,"liveIn":true},{"id":4,"code":"HCML","description":"Mileage","activityType":"N","hoursBased":false,"liveIn":false},{"id":5,"code":"HC TRN","description":"Training","activityType":"N","hoursBased":true,"liveIn":false}]
		}
	###
	2. GET /api/activityrecords
	request params:

	{    
		branchIds: []<int> #mandatory
		dateFrom: <date> #mandatory
		dateTo: <date> #mandatory
		clientNameFilter: <string> #optional
		employeeNameFilter: <string> #optional
		sortBy: <string> #optional, format: 'field;direction'
		direction: <string> #optional, format: 'field;direction'
		offset: <int> #optional, index of the first element to transfer from the result set
		count: <int> #optional, count of elements to transfer
	}
	response params:
	[
		{
			id: <int>, #mandatory
			lockedBy: { id: <int>, loginName: <string> }
			client: #mandatory
				{
					id: <int>, #mandatory
					fname: <string>
					lname: <string>
					address: <string>
					phone: <string>
					language: <string>
				}
			employee: optional   #employee info
				{
					id: <int>
					fname: <string>
					lname: <string>
					address: <string>
					phone: <string>

				}     
			date: <date> #mandatory
			timeFrom: <time> #optional
			timeTo: <time> #optional
			duration: <time>
			scheduledDuration: <time>
			validationResult: [{}]
		}
	] 
	###

	app.get '/api/activityrecords/:id', (req, res) ->
		id = req.params.id
		if req.query.force
          for visit, i in visits
            if parseInt(visits[i].id, 10) is parseInt(id, 10)
              visits[i].lockedByUserId = userInfo.userId
              visits[i].lockedByUserName = userInfo.login
              _socket.emit 'recordLocked', { "activityRecordId": req.query.activityRecordId, "userId": userInfo.userId, "userName": userInfo.firstName + " " + userInfo.lastName}
              res.json visits[i]
              break
		else
			res.json visit for visit in visits when parseInt(visit.id, 10) is parseInt(id, 10)

	app.get '/api/activityrecords', (req, res) ->
		result = visits
		# filters
		
		if req.query.dateFrom
			result = result.filter (x) -> x.date == req.query.date

		if req.query.dateTo
			result = result.filter (x) -> x.date == req.query.date

		if req.query.clientNameFilter
			result = result.filter (x) -> (x.client.firstName.indexOf(req.query.clientNameFilter) != -1) || (x.client.lastName.indexOf(req.query.clientNameFilter) != -1)

		if req.query.employeeNameFilter
			result = result.filter (x) -> (x.client.firstName.indexOf(req.query.employeeNameFilter) != -1) || (x.client.lastName.indexOf(req.query.clientNameFilter) != -1) 

		# sorting
		if req.query.sortBy
			result.sort (a, b) ->
				a[req.query.sortBy] - b[req.query.sortBy]

		if req.query.direction == 'desc'
			result.reverse()
		
		# pagination

		if req.query.offset
			offset = parseInt(req.query.offset)
			limit = count = parseInt(req.query.count)
			limit = limit + offset-1 if offset < 1
			result = result.filter (x) -> offset <= x.client.id < offset + count
			console.log "request #{req.query.count} starting from #{offset} returning #{result.length}"

		res.json result 
	

 ###
	3. POST api/activityrecords:id
	request params:
	{
		id: <int>, #mandatory
		<...>
	}
	response params:
	{
		post: "Success"
	}
 ###

	app.post '/api/activityrecords', (req, res) ->
        id = req.body.id
        for visit, i in visits
          if parseInt(visits[i].id, 10) is parseInt(id, 10)
            visits[i].duration = req.body.duration
            visits[i].startTime = req.body.startTime
            visits[i].endTime = req.body.endTime
            visits[i].errandDistance = req.body.errandDistance
            visits[i].travelDistance = req.body.travelDistance
            visits[i].travelTime = req.body.travelTime
            visits[i].serviceCode = req.body.serviceCode
            res.json visits[i]
            break

 ###
	4. GET api/visits/:visitid
	request params:
	{
		id: <boolean>, #mandatory
	}
	response params:
	{
		id: <int>, #mandatory
		client: #mandatory
			{
				id: <int>, #mandatory
				lockedBy: { id: <int>, loginName: <string> }
				messageSent: <dateTime>
				fname: <string>
				lname: <string>
				address: <string>
				phone: <string>
				language: <string>
				#preferences?
			}   
		assignedTo: #optional
			{
				id: <int>
				fname: <string>
				lname: <string>
				address: <string>
				phone: <string>
				language: <string>
				skills: []
				latitude: <double>
				longitude: <double>
			}   
		date: <date> #mandatory
		timeFrom: <time>
		timeTo: <time>
	}
 ###

	app.get '/api/visits/:id', (req, res) ->
		id = req.params.id
		res.json visit for visit in visits when parseInt(visit.id, 10) is parseInt(id, 10)

	###
	6. GET api/candidates
	request params:
	{
		visitId: <int>
		status: <string>
		skills: []<string>
		typeList: []<string>
			
	}
	response params:
	[
		{
			candidate:
				{
					id: <int>
					fname: <string>
					lname: <string>
					address: <string>
					phone: <string>
					language: <string>
					calendar: [{from: <dateTime> ,to: <dateTime>}]
					skills: {}
					latitude: <double>
					longitude: <double>
				}         
			score: <float>
			distance: <float>
			visitId: <int>
			selectedAs: { direct: <boolean>, byScore: <boolean>, favorite: <boolean>, recent: <boolean> }
		},
	]
	###



	###

	employeeId: <int>
  action: assign/rejected/rescheduld
  from: <dateTime> #optional
  to: <dateTime> #optional
  declineReasonId: <int> #optional
  declineMessage: <string> #optional

	###
	
	app.post '/api/visits', (req, res) ->
		console.log req.body
		_socket.emit 'visitLocked', { VisitId: 1, UserId: 100, LoginName: "Kirill", Time: new Date(), PrevUserId: 100 }


	app.get '/api/employees', (req, res) ->
		visitid = req.query.visitid
		result = candidates
		# filters
		#console.log req.params.visitid
		#result = result.filter (x) -> x.visitId == parseInt(req.query.visitid, 10)

		#if req.query.date
		#result = result.filter (x) -> x.date == req.query.date

		if req.query.candidateNameFilter
			result = result.filter (x) -> x.employee.firstName.indexOf(req.query.candidateNameFilter) != -1 


		if req.query.skills
			skills = req.query.skills.split(';')
			tmp = []
			for i in skills
				tmp = result.filter (x) -> x.employee.skills.indexOf(i) != -1
				tmp.concat(tmp)
			result = tmp	
				
		# sorting
		if req.query.sortBy
			result.sort (a, b) ->
				a[req.query.sortBy] - b[req.query.sortBy]

		if req.query.direction == 'desc'
			result.reverse()

		# pagination
		if req.query.offset
			offset = parseInt(req.query.offset)
			limit = count = parseInt(req.query.count)
			limit = limit + offset-1 if offset < 1
			result = result.filter (x) -> offset <= x.employee.id < offset + count
			console.log "request #{req.query.count} starting from #{offset} returning #{result.length}"

		(delete candidate['visitId']) for candidate in result

		res.json [{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"isAmpUser":false,"id":5,"firstName":"Inna","lastName":"Berkovich","address":"Adr","phone":"1111111"}]

	app.get '/api/clients', (req, res) ->
		res.json [{"branch":{"id":1,"name":"320 - MetroSouth","roundingModulo":15},"isAmpUser":false,"id":5,"firstName":"Samanta","lastName":"Cruz","address":"Address","phone":"2222222"}]


	app.get '/api/userInfo', (req, res) ->
		res.json userInfo


 ###
	7. POST api/notes
	request params:
	{
		activityRecordId: <int>, #mandatory
		author: <string>,
		createdAt: <string>,
		text: <string>,
	response params:
	{
		post: "Success"
	}
 ###

	app.post '/api/notes', (req, res) ->
		id = req.body.activityRecordId
		for visit, i in visits
          if parseInt(visits[i].id, 10) is parseInt(id, 10)
            visits[i].visitNotes.push("author": req.body.author,"createdAt": req.body.createdAt,"text": req.body.text)
            res.json visits[i]
            break
	###
	8. GET api/releaselock
	request params:
	{
		activityRecordId: <int>, #mandatory
	}
	response params:
	{
		get: "Success"
	}
	###

	app.get '/api/releaselock', (req, res) ->
		_socket.emit 'lockReleased', { "activityRecordId": req.query.activityRecordId, "userId": userInfo.login }
		res.json 
			"get": "Success for " + req.query.activityRecordId


	###
	9. PUT api/activityrecords
	request params:
	{
		activityRecordId: <int>, #mandatory
		clientId: <int>, #mandatory
		employeeId: <int>
		endTime: <YYYY-MM-DD HH:MM:CC>, #mandatory
		errandDistance: <int>
		serviceCode: <string>
		startTime: <YYYY-MM-DD HH:MM:CC>, #mandatory
		travelDistance: <int>
		travelTime: <time(HH:MM)>
		visitNotes: []
	}
	response params:
	{
		PUT: "Success"
	}
	###

	app.put '/api/activityrecords', (req, res) ->
		_visit = req.body
		_visit.id = visits.length + 1
		_visit.client = {}
		_visit.client.id = randomInt(1,5)
		visits.push _visit
		_socket.emit 'newRecord'
		res.json _visit
			
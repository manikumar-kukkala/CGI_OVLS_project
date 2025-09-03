const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
app.use(cors());
app.use(bodyParser.json());

// Fake database (in-memory)
let users =  [{ username: 'admin', password: 'admin@123', role: 'admin' }];
let applications = [
  { id: 1, name: 'John Doe', status: 'pending' },
  { id: 2, name: 'Alice Smith', status: 'approved' },
  { id: 3, name: 'Bob Johnson', status: 'rejected' },
  { id: 4, name: 'Emily Watson', status: 'pending' },
  { id: 5, name: 'Tom Hardy', status: 'approved' }
];
// Register API
app.post('/api/auth/register', (req, res) => {
  const user = req.body;
  users.push(user);
  res.json({ message: 'Registration successful', user });

});

// Login API
app.post('/api/auth/login', (req, res) => {
 
  const { username, password } = req.body;
  const user = users.find(u => u.username == username && u.password == password);



  if (!user) {
      console.log(user);
    return res.status(400).json({ message: 'Invalid' });
  }

  res.json({ message: 'Login successful', user });
});

// Get all applications
app.get('/api/admin/applications', (req, res) => {
 
  res.json({applications});
});

// Get applications by status
app.get('/api/admin/status', (req, res) => {
  const stats = {
    pending: applications.filter(app => app.status === 'pending').length,
    approved: applications.filter(app => app.status === 'approved').length,
    rejected: applications.filter(app => app.status === 'rejected').length,
    total: applications.length
  };
  res.json({ stats });
});

app.put('/api/applications/:id', (req, res) => {
  
  const id = parseInt(req.params.id);
  const { status } = req.body;

  const appIndex = applications.findIndex(app => app.id === id);
  if (appIndex === -1) {
    return res.status(404).json({ message: 'Application not found' });
  }

  applications[appIndex].status = status;
  res.json({ message: 'Status updated', application: applications[appIndex] });
});


// Start server
app.listen(5000, () => console.log('✅ Server running on http://localhost:5000'));
